import { Component, OnInit, ViewChild, ElementRef, AfterViewChecked } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import * as marked from 'marked';
import { ChatbotService, ChatResponse } from '../services/chatbot.service';

interface ChatMessage {
  text: string;
  sender: 'user' | 'bot';
  timestamp: Date;
  isHtml?: boolean;
}

// Using ChatResponse from chatbot.service.ts

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
    private chatbotService: ChatbotService,
    private fb: FormBuilder
  ) {
    this.chatForm = this.fb.group({
      message: ['', [Validators.required, Validators.maxLength(500)]]
    });
  }

  ngOnInit(): void {
    this.loadConversation();
    if (this.messages.length === 0) {
      this.addBotMessage("👋 Hello! I'm your PFE assistant. How can I help you today?", false);
    }

    // Check if the chatbot should be automatically opened
    const lastOpenTime = localStorage.getItem('chatbot_last_open');
    if (!lastOpenTime || (Date.now() - parseInt(lastOpenTime)) > 24 * 60 * 60 * 1000) {
      // Auto-open after 1 second if not opened in the last 24 hours
      setTimeout(() => {
        this.isOpen = true;
      }, 1000);
    }
  }

  ngAfterViewChecked(): void {
    if (!this.isUserScrollingUp) {
      this.scrollToBottom();
    }
  }

  toggleChat() {
    this.isOpen = !this.isOpen;

    if (this.isOpen) {
      // Save the last open time
      localStorage.setItem('chatbot_last_open', Date.now().toString());

      // Show suggested questions on first interaction
      if (this.firstInteraction) {
        this.suggestedQuestions = [
          "What is PFESpace?",
          "How does CV analysis work?",
          "How to apply for a project?",
          "What projects are available?"
        ];
        this.firstInteraction = false;
      }

      // Focus the input field when opened
      setTimeout(() => {
        const inputElement = document.querySelector('.message-form input') as HTMLInputElement;
        if (inputElement) {
          inputElement.focus();
        }
      }, 300);
    }
  }

  async sendMessage() {
    const userMessage = this.chatForm.get('message')?.value.trim();
    if (!userMessage || this.isLoading) return;

    this.addUserMessage(userMessage);
    this.chatForm.reset();
    this.isLoading = true;
    this.suggestedQuestions = [];

    // Scroll to bottom after adding user message
    this.scrollToBottom();

    // Add a small delay to simulate thinking
    await this.delay(600);

    try {
      const response = await this.chatbotService.sendMessage(userMessage).toPromise();

      if (response) {
        await this.addBotMessage(response.responseText, true);

        // Add a small delay before showing suggestions
        await this.delay(300);
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

  private delay(ms: number): Promise<void> {
    return new Promise(resolve => setTimeout(resolve, ms));
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
        // Configure marked options for better rendering
        marked.setOptions({
          breaks: true,        // Add line breaks
          gfm: true           // Use GitHub Flavored Markdown
        });

        messageContent = await marked.parse(text);

        // Enhance links to open in new tab
        messageContent = messageContent.replace(
          /<a /g,
          '<a target="_blank" rel="noopener noreferrer" '
        );

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

    // Play a subtle notification sound when a message is received
    this.playNotificationSound();
  }

  private playNotificationSound() {
    try {
      // Create a simple notification sound using the Web Audio API
      const audioContext = new (window.AudioContext || (window as any).webkitAudioContext)();
      const oscillator = audioContext.createOscillator();
      const gainNode = audioContext.createGain();

      oscillator.type = 'sine';
      oscillator.frequency.setValueAtTime(880, audioContext.currentTime); // A5 note

      gainNode.gain.setValueAtTime(0.1, audioContext.currentTime); // Low volume
      gainNode.gain.exponentialRampToValueAtTime(0.001, audioContext.currentTime + 0.5);

      oscillator.connect(gainNode);
      gainNode.connect(audioContext.destination);

      oscillator.start();
      oscillator.stop(audioContext.currentTime + 0.5);
    } catch (e) {
      console.log('Audio notification not supported');
    }
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