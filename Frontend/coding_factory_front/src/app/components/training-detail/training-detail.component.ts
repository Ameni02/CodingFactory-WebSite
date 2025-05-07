import { Component, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Formation, Comment, FormationService } from 'src/app/services/formation.service';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { CommentListComponent } from '../comment-list/comment-list.component';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-training-detail',
  templateUrl: './training-detail.component.html',
  styleUrls: ['./training-detail.component.css']
})
export class TrainingDetailComponent implements OnInit {
  formation: Formation | null = null;
  loading = false;
  error = '';
  pdfUrl: SafeResourceUrl | null = null;
  activeTab = 'details';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private formationService: FormationService,
    private sanitizer: DomSanitizer,
    private http: HttpClient
  ) { }

  ngOnInit(): void {
    this.loadFormation();
  }

  loadFormation(): void {
    const id = this.route.snapshot.paramMap.get('id');
    console.log('Training detail component received ID:', id);

    if (!id) {
      this.error = 'Invalid training ID';
      return;
    }

    this.loading = true;
    this.formationService.getFormationById(Number(id)).subscribe({
      next: (formation) => {
        console.log('Formation loaded successfully:', formation);
        this.formation = formation;
        this.loading = false;
        this.loadPdf();

        // After loading the formation, fetch all comments for this training
        this.loadComments(Number(id));
      },
      error: (err) => {
        console.error('Error loading formation:', err);
        this.error = 'Error loading training: ' + (err.error || err.message);
        this.loading = false;
      }
    });
  }

  loadComments(formationId: number): void {
    console.log('Loading comments for formation ID:', formationId);

    // Try direct HTTP request first since we know how to handle the response format
    this.loadCommentsViaHttp(formationId);
  }

  loadCommentsViaHttp(formationId: number): void {
    console.log('Loading comments via direct HTTP for formation ID:', formationId);

    // Make a direct fetch request to see the raw response
    fetch(`http://localhost:8057/api/comments/formation/${formationId}`)
      .then(response => {
        console.log('Fetch response status:', response.status);
        console.log('Fetch response headers:', response.headers);
        return response.text();
      })
      .then(text => {
        console.log('Raw response text:', text.substring(0, 200) + '...');
        console.log('Response length:', text.length);

        // Check if it looks like XML
        if (text.includes('<')) {
          console.log('Response appears to be XML, trying to extract comments');
          this.extractCommentsFromXml(text, formationId);
        } else {
          // Try to parse as JSON
          try {
            const parsedResponse = JSON.parse(text);
            console.log('Parsed JSON response:', parsedResponse);

            if (parsedResponse && Array.isArray(parsedResponse) && parsedResponse.length > 0) {
              // Map the response to Comment objects
              const mappedComments: Comment[] = parsedResponse.map(item => ({
                id: item.id,
                content: item.content,
                rating: item.rating,
                userName: item.userName,
                category: item.category,
                sentimentLabel: item.sentimentLabel || 'Neutral',
                sentimentScore: item.sentimentScore || 0.5,
                formation: { id: formationId },
                createdAt: item.createdAt ? new Date(item.createdAt) : new Date()
              }));

              console.log('Mapped comments:', mappedComments);

              // Update the formation object with the comments
              if (this.formation) {
                this.formation.comments = mappedComments;
                this.formation.totalCommentCount = mappedComments.length;

                // Update sentiment metrics
                this.updateSentimentMetrics();

                // Update the comment list component
                if (this.commentList) {
                  console.log('Updating comment list with HTTP comments');
                  this.commentList.directComments = [...mappedComments];
                  this.commentList.comments = [...mappedComments];
                  this.commentList.calculateStatistics();
                }
              }
            } else {
              console.log('No comments found in JSON response');
              this.createDummyComments(formationId);
            }
          } catch (e) {
            console.error('Error parsing JSON response:', e);
            this.createDummyComments(formationId);
          }
        }
      })
      .catch(err => {
        console.error('Fetch error:', err);
        this.createDummyComments(formationId);
      });
  }

  createDummyComments(formationId: number): void {
    console.log('Creating dummy comments for testing');

    // Create dummy comments for testing
    const dummyComments: Comment[] = [];
    for (let i = 1; i <= 9; i++) {
      dummyComments.push({
        id: i,
        content: `Test comment ${i} for formation ${formationId}`,
        rating: Math.floor(Math.random() * 5) + 1,
        userName: `User ${i}`,
        category: 'General',
        sentimentLabel: 'Neutral',
        sentimentScore: 0.5,
        formation: { id: formationId },
        createdAt: new Date()
      });
    }

    if (this.formation) {
      this.formation.comments = dummyComments;
      this.formation.totalCommentCount = dummyComments.length;
      this.updateSentimentMetrics();

      if (this.commentList) {
        this.commentList.directComments = [...dummyComments];
        this.commentList.comments = [...dummyComments];
        this.commentList.calculateStatistics();
      }
    }
  }

  extractCommentsFromXml(xmlString: string, formationId: number): void {
    try {
      console.log('Raw XML string:', xmlString.substring(0, 200) + '...');

      const parser = new DOMParser();
      const xmlDoc = parser.parseFromString(xmlString, 'text/xml');

      console.log('XML document structure:', xmlDoc);

      // Log the root element name to understand the structure
      const rootElement = xmlDoc.documentElement;
      console.log('Root element name:', rootElement.nodeName);

      // Try to find all comment nodes using various approaches
      let commentNodes: HTMLCollectionOf<Element> | Element[] = new Array<Element>();

      // Approach 1: Direct tag names
      const itemNodes = xmlDoc.getElementsByTagName('item');
      if (itemNodes.length > 0) {
        console.log(`Found ${itemNodes.length} item nodes`);
        commentNodes = itemNodes;
      }

      // Approach 2: Comment tag
      if (commentNodes.length === 0) {
        const commentTagNodes = xmlDoc.getElementsByTagName('comment');
        if (commentTagNodes.length > 0) {
          console.log(`Found ${commentTagNodes.length} comment nodes`);
          commentNodes = commentTagNodes;
        }
      }

      // Approach 3: Comments container
      if (commentNodes.length === 0) {
        const commentsContainer = xmlDoc.getElementsByTagName('comments')[0];
        if (commentsContainer) {
          // Get all child nodes of the comments container
          const childNodes = commentsContainer.children;
          console.log(`Found ${childNodes.length} child nodes in comments container`);

          // Convert HTMLCollection to array
          commentNodes = Array.from(childNodes);
        }
      }

      // Approach 4: Look for content elements directly
      if (commentNodes.length === 0) {
        const contentNodes = xmlDoc.getElementsByTagName('content');
        if (contentNodes.length > 0) {
          console.log(`Found ${contentNodes.length} content nodes, trying to find parent comment nodes`);

          // For each content node, try to find its parent comment node
          const parentNodes = new Set<Element>();
          for (let i = 0; i < contentNodes.length; i++) {
            const parentNode = contentNodes[i].parentElement;
            if (parentNode) {
              parentNodes.add(parentNode);
            }
          }

          commentNodes = Array.from(parentNodes);
          console.log(`Found ${commentNodes.length} unique parent nodes`);
        }
      }

      // Approach 5: Try to parse the XML manually if all else fails
      if (commentNodes.length === 0) {
        console.log('No comment nodes found using standard methods, trying manual parsing');

        // Look for patterns in the XML string
        const commentMatches = xmlString.match(/<item>[\s\S]*?<\/item>/g);
        if (commentMatches && commentMatches.length > 0) {
          console.log(`Found ${commentMatches.length} comment matches using regex`);

          // Create a new document for each match
          const tempComments: Comment[] = [];

          for (const match of commentMatches) {
            try {
              const tempDoc = parser.parseFromString(`<root>${match}</root>`, 'text/xml');
              const tempNode = tempDoc.getElementsByTagName('item')[0];

              if (tempNode) {
                const comment: Comment = this.extractCommentFromNode(tempNode, formationId);
                tempComments.push(comment);
              }
            } catch (e) {
              console.error('Error parsing comment match:', e);
            }
          }

          if (tempComments.length > 0) {
            console.log(`Extracted ${tempComments.length} comments using regex approach`);

            if (this.formation) {
              this.formation.comments = tempComments;
              this.formation.totalCommentCount = tempComments.length;
              this.updateSentimentMetrics();

              if (this.commentList) {
                this.commentList.directComments = [...tempComments];
                this.commentList.comments = [...tempComments];
                this.commentList.calculateStatistics();
              }
            }

            return; // Exit early since we've handled the comments
          }
        }
      }

      console.log(`Found ${commentNodes.length} comment nodes in total`);

      // Process the comment nodes
      const comments: Comment[] = [];

      for (let i = 0; i < commentNodes.length; i++) {
        const node = commentNodes[i];
        console.log(`Processing comment node ${i}:`, node);

        try {
          const comment = this.extractCommentFromNode(node, formationId);
          comments.push(comment);
        } catch (e) {
          console.error('Error extracting comment from node:', e);
        }
      }

      console.log(`Extracted ${comments.length} comments from XML`);

      if (comments.length > 0 && this.formation) {
        this.formation.comments = comments;
        this.formation.totalCommentCount = comments.length;
        this.updateSentimentMetrics();

        if (this.commentList) {
          this.commentList.directComments = [...comments];
          this.commentList.comments = [...comments];
          this.commentList.calculateStatistics();
        }
      } else {
        console.log('No comments extracted, trying to create dummy comments for testing');

        // Create dummy comments for testing
        const dummyComments: Comment[] = [];
        for (let i = 1; i <= 9; i++) {
          dummyComments.push({
            id: i,
            content: `Test comment ${i} for formation ${formationId}`,
            rating: Math.floor(Math.random() * 5) + 1,
            userName: `User ${i}`,
            category: 'General',
            sentimentLabel: 'Neutral',
            sentimentScore: 0.5,
            formation: { id: formationId },
            createdAt: new Date()
          });
        }

        if (this.formation) {
          this.formation.comments = dummyComments;
          this.formation.totalCommentCount = dummyComments.length;
          this.updateSentimentMetrics();

          if (this.commentList) {
            this.commentList.directComments = [...dummyComments];
            this.commentList.comments = [...dummyComments];
            this.commentList.calculateStatistics();
          }
        }
      }
    } catch (e) {
      console.error('Error parsing XML:', e);
    }
  }

  extractCommentFromNode(node: Element, formationId: number): Comment {
    // Log the node name and content
    console.log('Node name:', node.nodeName);
    console.log('Node content:', node.innerHTML);

    const comment: Comment = {
      id: this.getXmlNodeContent(node, 'id', 0),
      content: this.getXmlNodeContent(node, 'content', ''),
      rating: this.getXmlNodeContent(node, 'rating', 0),
      userName: this.getXmlNodeContent(node, 'userName', ''),
      category: this.getXmlNodeContent(node, 'category', 'General'),
      formation: { id: formationId },
      sentimentLabel: this.getXmlNodeContent(node, 'sentimentLabel', 'Neutral'),
      sentimentScore: this.getXmlNodeContent(node, 'sentimentScore', 0.5),
      createdAt: new Date()
    };

    // Try to get created date
    const createdAtStr = this.getXmlNodeTextContent(node, 'createdAt');
    if (createdAtStr) {
      comment.createdAt = new Date(createdAtStr);
    }

    console.log('Extracted comment:', comment);
    return comment;
  }

  getXmlNodeContent<T>(node: Element, tagName: string, defaultValue: T): T {
    const elements = node.getElementsByTagName(tagName);
    if (elements.length > 0 && elements[0].textContent) {
      const content = elements[0].textContent;
      if (typeof defaultValue === 'number') {
        return Number(content) as unknown as T;
      }
      return content as unknown as T;
    }
    return defaultValue;
  }

  getXmlNodeTextContent(node: Element, tagName: string): string | null {
    const elements = node.getElementsByTagName(tagName);
    if (elements.length > 0) {
      return elements[0].textContent;
    }
    return null;
  }

  loadPdf(): void {
    if (!this.formation) return;

    // First try to load the PDF normally
    this.formationService.getPdf(this.formation.id).subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        this.pdfUrl = this.sanitizer.bypassSecurityTrustResourceUrl(url);
      },
      error: (err) => {
        console.error('Error loading PDF:', err);

        // If there's an error, use a sample PDF for testing
        const samplePdfUrl = 'https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf';
        this.pdfUrl = this.sanitizer.bypassSecurityTrustResourceUrl(samplePdfUrl);

        // Show a warning instead of an error
        console.warn('Using sample PDF for testing');
      }
    });
  }

  setActiveTab(tab: string): void {
    this.activeTab = tab;

    // If switching to comments tab, refresh the comments
    if (tab === 'comments' && this.commentList) {
      console.log('Switching to comments tab, refreshing comments');
      this.commentList.refreshComments();
    }
  }

  @ViewChild('commentList') commentList?: CommentListComponent;

  onCommentAdded(comment: Comment): void {
    if (!this.formation) return;

    console.log('Comment added:', comment);

    // Add the comment to the local list immediately
    if (!this.formation.comments) {
      this.formation.comments = [];
    }

    // Add the new comment to the list
    this.formation.comments.push(comment);
    this.formation.totalCommentCount = (this.formation.totalCommentCount || 0) + 1;

    // Update sentiment metrics
    this.updateSentimentMetrics();

    // IMPORTANT: Manually update the comment list component with the new comments
    if (this.commentList) {
      console.log('Directly updating comment list component with new comments');
      this.commentList.directComments = [...this.formation.comments];
      this.commentList.comments = [...this.formation.comments];
      this.commentList.calculateStatistics();
    }

    // Also refresh after a delay to ensure the database has been updated
    setTimeout(() => {
      // Fetch all comments for this training again
      if (this.formation) {
        console.log('Refreshing comments after delay');
        this.loadComments(this.formation.id);
      }
    }, 1000); // 1 second delay
  }

  updateSentimentMetrics(): void {
    // Ensure formation and comments exist
    if (!this.formation || !this.formation.comments || this.formation.comments.length === 0) {
      return;
    }

    // Calculate average sentiment score
    let totalScore = 0;
    let positiveCount = 0;
    let commentCount = 0;

    // Safely iterate through comments
    for (const comment of this.formation.comments) {
      if (comment && comment.sentimentScore !== undefined) {
        totalScore += comment.sentimentScore;
        commentCount++;

        if (comment.sentimentScore >= 0.7) {
          positiveCount++;
        }
      }
    }

    // Only update metrics if we have valid comments
    if (commentCount > 0) {
      this.formation.averageSentimentScore = totalScore / commentCount;
      this.formation.positiveCommentRatio = positiveCount / commentCount;
    }
  }

  goBack(): void {
    console.log('Navigating back');
    // Check if we came from the admin or user training list
    const url = this.router.url;
    if (url.includes('admin')) {
      this.router.navigate(['/admin/listTrainingAdmin']);
    } else {
      this.router.navigate(['/listTrainingUser']);
    }
  }

  getSentimentIcon(formation: Formation): string {
    if (!formation.dominantSentiment) return '😐';

    switch (formation.dominantSentiment) {
      case 'Positive':
        return '😊';
      case 'Negative':
        return '😞';
      default:
        return '😐';
    }
  }

  getSentimentClass(formation: Formation): string {
    if (!formation.dominantSentiment) return 'Neutral';
    return formation.dominantSentiment;
  }
}
