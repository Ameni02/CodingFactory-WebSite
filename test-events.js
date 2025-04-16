// Simple script to test if the events API is working
const fetch = require('node-fetch');

async function testEvents() {
  try {
    console.log('Testing events API...');
    
    // Test the debug endpoints
    const countsResponse = await fetch('http://localhost:8090/api/v1/debug/events/count');
    const counts = await countsResponse.json();
    console.log('Event counts:', counts);
    
    // Test the images endpoint
    const imagesResponse = await fetch('http://localhost:8090/api/v1/debug/events/images');
    const images = await imagesResponse.json();
    console.log('Event images info:', JSON.stringify(images, null, 2));
    
    // Test the active events endpoint
    const eventsResponse = await fetch('http://localhost:8090/api/v1/events/active');
    const events = await eventsResponse.json();
    console.log('Active events:', events);
    
    console.log('Tests completed successfully');
  } catch (error) {
    console.error('Error testing events API:', error);
  }
}

testEvents();
