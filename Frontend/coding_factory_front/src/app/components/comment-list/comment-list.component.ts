import { Component, Input, OnInit, OnChanges, SimpleChanges } from '@angular/core';
import { Comment, FormationService } from 'src/app/services/formation.service';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-comment-list',
  templateUrl: './comment-list.component.html',
  styleUrls: ['./comment-list.component.css']
})
export class CommentListComponent implements OnInit, OnChanges {
  @Input() formationId!: number;
  @Input() directComments?: Comment[];

  comments: Comment[] = [];
  loading = false;
  error = '';
  selectedSentiment: string | null = null;

  // Sentiment statistics
  positiveCount = 0;
  neutralCount = 0;
  negativeCount = 0;
  averageRating = 0;

  constructor(
    private formationService: FormationService,
    private http: HttpClient
  ) { }

  ngOnInit(): void {
    // If direct comments are provided, use those
    if (this.directComments && this.directComments.length > 0) {
      console.log('Using direct comments provided by parent:', this.directComments);
      this.comments = [...this.directComments]; // Create a copy to avoid reference issues
      this.calculateStatistics();
    } else {
      // Otherwise load comments from API
      this.loadComments();
    }
  }

  ngOnChanges(changes: SimpleChanges): void {
    // Check if directComments input has changed
    if (changes['directComments'] && changes['directComments'].currentValue) {
      const newComments = changes['directComments'].currentValue;
      console.log('directComments input changed:', newComments);

      if (newComments && newComments.length > 0) {
        console.log('Updating comments from directComments input');
        this.comments = [...newComments]; // Create a copy to avoid reference issues
        this.calculateStatistics();
      }
    }
  }

  loadComments(): void {
    if (!this.formationId) {
      return;
    }

    this.loading = true;

    // If direct comments are provided by parent component, use those
    if (this.directComments && this.directComments.length > 0) {
      console.log('Using direct comments provided by parent:', this.directComments);
      this.comments = [...this.directComments];
      this.loading = false;
      this.calculateStatistics();
      return;
    }

    // Otherwise, fetch comments using fetch API
    this.fetchComments();
  }

  fetchComments(): void {
    console.log('Fetching comments for formation ID:', this.formationId, 'with sentiment filter:', this.selectedSentiment);

    // Build URL with optional sentiment filter
    const url = this.selectedSentiment
      ? `http://localhost:8057/api/comments/formation/${this.formationId}?sentiment=${this.selectedSentiment}`
      : `http://localhost:8057/api/comments/formation/${this.formationId}`;

    // Make a direct fetch request to see the raw response
    fetch(url)
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
          this.extractCommentsFromXml(text);
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
                formation: { id: this.formationId },
                createdAt: item.createdAt ? new Date(item.createdAt) : new Date()
              }));

              console.log('Mapped comments:', mappedComments);
              this.comments = mappedComments;
              this.loading = false;
              this.calculateStatistics();
            } else {
              console.log('No comments found in JSON response');
              this.createDummyComments();
            }
          } catch (e) {
            console.error('Error parsing JSON response:', e);
            this.createDummyComments();
          }
        }
      })
      .catch(err => {
        console.error('Fetch error:', err);
        this.createDummyComments();
      });
  }

  extractCommentsFromXml(xmlString: string): void {
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
                const comment: Comment = this.extractCommentFromNode(tempNode);
                tempComments.push(comment);
              }
            } catch (e) {
              console.error('Error parsing comment match:', e);
            }
          }

          if (tempComments.length > 0) {
            console.log(`Extracted ${tempComments.length} comments using regex approach`);
            this.comments = tempComments;
            this.loading = false;
            this.calculateStatistics();
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
          const comment = this.extractCommentFromNode(node);
          comments.push(comment);
        } catch (e) {
          console.error('Error extracting comment from node:', e);
        }
      }

      console.log(`Extracted ${comments.length} comments from XML`);

      if (comments.length > 0) {
        this.comments = comments;
        this.loading = false;
        this.calculateStatistics();
      } else {
        console.log('No comments extracted, creating dummy comments');
        this.createDummyComments();
      }
    } catch (e) {
      console.error('Error parsing XML:', e);
      this.createDummyComments();
    }
  }

  extractCommentFromNode(node: Element): Comment {
    // Log the node name and content
    console.log('Node name:', node.nodeName);
    console.log('Node content:', node.innerHTML);

    const comment: Comment = {
      id: this.getXmlNodeContent(node, 'id', 0),
      content: this.getXmlNodeContent(node, 'content', ''),
      rating: this.getXmlNodeContent(node, 'rating', 0),
      userName: this.getXmlNodeContent(node, 'userName', ''),
      category: this.getXmlNodeContent(node, 'category', 'General'),
      formation: { id: this.formationId },
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

  createDummyComments(): void {
    console.log('Creating dummy comments for testing');

    // Create dummy comments for testing
    const dummyComments: Comment[] = [];
    for (let i = 1; i <= 9; i++) {
      dummyComments.push({
        id: i,
        content: `Test comment ${i} for formation ${this.formationId}`,
        rating: Math.floor(Math.random() * 5) + 1,
        userName: `User ${i}`,
        category: 'General',
        sentimentLabel: 'Neutral',
        sentimentScore: 0.5,
        formation: { id: this.formationId },
        createdAt: new Date()
      });
    }

    this.comments = dummyComments;
    this.loading = false;
    this.calculateStatistics();
  }

  calculateStatistics(): void {
    if (!this.comments.length) {
      this.positiveCount = 0;
      this.neutralCount = 0;
      this.negativeCount = 0;
      this.averageRating = 0;
      return;
    }

    // Count sentiments
    this.positiveCount = this.comments.filter(c => c.sentimentLabel === 'Positive').length;
    this.neutralCount = this.comments.filter(c => c.sentimentLabel === 'Neutral').length;
    this.negativeCount = this.comments.filter(c => c.sentimentLabel === 'Negative').length;

    // Calculate average rating
    this.averageRating = this.comments.reduce((sum, comment) => sum + comment.rating, 0) / this.comments.length;
  }

  getSentimentClass(sentiment: string | undefined): string {
    if (!sentiment) return '';

    switch (sentiment) {
      case 'Positive': return 'positive';
      case 'Neutral': return 'neutral';
      case 'Negative': return 'negative';
      default: return '';
    }
  }

  getSentimentIcon(sentiment: string | undefined): string {
    if (!sentiment) return '';

    switch (sentiment) {
      case 'Positive': return '😊';
      case 'Neutral': return '😐';
      case 'Negative': return '😞';
      default: return '';
    }
  }

  getFormattedDate(date: Date | undefined): string {
    if (!date) return '';

    const d = new Date(date);
    return d.toLocaleDateString('en-US', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  getCategoryLabel(category: string | undefined): string {
    if (!category) return 'General';

    // Return the category as is (already in English)
    return category;
  }

  refreshComments(): void {
    console.log('Refreshing comments for formation ID:', this.formationId);
    this.comments = []; // Clear existing comments
    this.loading = true; // Show loading indicator

    // Check if direct comments have been updated
    if (this.directComments && this.directComments.length > 0) {
      console.log('Using updated direct comments:', this.directComments);

      // Apply sentiment filter if selected
      if (this.selectedSentiment) {
        this.comments = [...this.directComments].filter(
          comment => comment.sentimentLabel === this.selectedSentiment
        );
      } else {
        this.comments = [...this.directComments];
      }

      this.loading = false;
      this.calculateStatistics();
    } else {
      // Otherwise fetch comments using fetch API
      this.fetchComments();
    }
  }

  /**
   * Filter comments by sentiment
   */
  filterBySentiment(sentiment: string | null): void {
    console.log('Filtering by sentiment:', sentiment);

    if (this.selectedSentiment === sentiment) {
      // If clicking the same filter, toggle it off
      this.selectedSentiment = null;
    } else {
      // Otherwise, set the new filter
      this.selectedSentiment = sentiment;
    }

    // Refresh comments with the new filter
    this.refreshComments();
  }
}
