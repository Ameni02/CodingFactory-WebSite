import { Component, OnInit, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import * as marked from 'marked';

interface ChatMessage {
  text: string;
  sender: 'user' | 'bot';
  timestamp: Date;
  isHtml?: boolean;
}

interface ChatResponse {
  responseText: string;
  suggestedQuestions: string[];
  intent: string;
  timestamp: string;
  requiresAction: boolean;
  actionType: string;
}

@Component({
  selector: 'app-chatbot',
  templateUrl: './chatbot.component.html',
  styleUrls: ['./chatbot.component.css']
})
export class ChatbotComponent implements OnInit, AfterViewChecked {
  @ViewChild('messagesContainer') private messagesContainer!: ElementRef;
  
  chatForm: FormGroup;
  messages: ChatMessage[] = [];
  isOpen = false;
  isLoading = false;
  suggestedQuestions: string[] = [];
  firstInteraction = true;
  storageKey = 'chatbot_conversation';
  private isUserScrollingUp = false;
  private lastScrollTop = 0;

  constructor(
    private http: HttpClient, 
    private fb: FormBuilder
  ) {
    this.chatForm = this.fb.group({
      message: ['', [Validators.required, Validators.maxLength(500)]]
    });
  }

  ngOnInit(): void {
    this.loadConversation();
    if (this.messages.length === 0) {
      this.addBotMessage("Hello! I'm your PFE assistant. How can I help you today?", false);
    }
  }

  ngAfterViewChecked(): void {
    if (!this.isUserScrollingUp) {
      this.scrollToBottom();
    }
  }

  toggleChat() {
    this.isOpen = !this.isOpen;
    if (this.isOpen && this.firstInteraction) {
      this.suggestedQuestions = [
        "How to apply for a project?",
        "What projects are available?",
        "How to submit deliverables?"
      ];
      this.firstInteraction = false;
    }
  }

  async sendMessage() {
    const userMessage = this.chatForm.get('message')?.value.trim();
    if (!userMessage || this.isLoading) return;

    this.addUserMessage(userMessage);
    this.chatForm.reset();
    this.isLoading = true;
    this.suggestedQuestions = [];

    try {
      const response = await this.http.post<ChatResponse>(
        'http://localhost:8080/pfespace/api/chatbot/ask', 
        { message: userMessage }
      ).toPromise();

      if (response) {
        await this.addBotMessage(response.responseText, true);
        this.suggestedQuestions = response.suggestedQuestions || [];
        this.saveConversation();
      }
    } catch (err) {
      this.addBotMessage("⚠️ Sorry, I'm having trouble connecting to the server. Please try again later.", false);
      console.error('Chatbot error:', err);
    } finally {
      this.isLoading = false;
    }
  }

  useSuggestedQuestion(question: string) {
    this.chatForm.get('message')?.setValue(question);
    this.sendMessage();
  }

  clearConversation() {
    this.messages = [];
    localStorage.removeItem(this.storageKey);
    this.addBotMessage("Hello again! How can I help you today?", false);
    this.isUserScrollingUp = false;
  }

  onScroll(event: Event) {
    const element = event.target as HTMLElement;
    const currentScrollTop = element.scrollTop;
    
    // Detect if user is scrolling up
    if (currentScrollTop < this.lastScrollTop) {
      this.isUserScrollingUp = true;
    } 
    // Detect if user scrolled to bottom
    else if (element.scrollHeight - element.scrollTop === element.clientHeight) {
      this.isUserScrollingUp = false;
    }
    
    this.lastScrollTop = currentScrollTop;
  }

  private addUserMessage(text: string) {
    this.messages.push({
      text,
      sender: 'user',
      timestamp: new Date()
    });
    this.saveConversation();
  }

  private async addBotMessage(text: string, isMarkdown: boolean) {
    let messageContent: string;
    
    if (isMarkdown) {
      try {
        messageContent = await marked.parse(text);
      } catch (e) {
        console.error('Markdown parsing error:', e);
        messageContent = text;
      }
    } else {
      messageContent = text;
    }

    this.messages.push({
      text: messageContent,
      sender: 'bot',
      timestamp: new Date(),
      isHtml: isMarkdown
    });
    this.saveConversation();
  }

  private scrollToBottom(): void {
    setTimeout(() => {
      try {
        if (this.messagesContainer?.nativeElement) {
          const element = this.messagesContainer.nativeElement;
          element.scrollTop = element.scrollHeight;
        }
      } catch(err) {
        console.error('Scroll error:', err);
      }
    }, 0);
  }

  private saveConversation() {
    localStorage.setItem(this.storageKey, JSON.stringify({
      messages: this.messages,
      timestamp: new Date()
    }));
  }

  private loadConversation() {
    const conversationStr = localStorage.getItem(this.storageKey);
    if (conversationStr) {
      try {
        const conversation = JSON.parse(conversationStr);
        
        const TWENTY_FOUR_HOURS = 24 * 60 * 60 * 1000;
        const conversationDate = new Date(conversation.timestamp);
        const isRecent = new Date().getTime() - conversationDate.getTime() < TWENTY_FOUR_HOURS;
        
        if (isRecent && conversation.messages) {
          this.messages = conversation.messages.map((msg: any) => ({
            ...msg,
            timestamp: new Date(msg.timestamp)
          }));
        } else {
          localStorage.removeItem(this.storageKey);
        }
      } catch (e) {
        console.error('Error loading conversation:', e);
        localStorage.removeItem(this.storageKey);
      }
    }
  }
}