<template>
  <div class="webflux-demo">
    <h1>Spring WebFlux Demo</h1>

    <div class="section">
      <h2>Mono Example</h2>
      <button @click="fetchMono">Get Mono Data</button>
      <p v-if="monoData">Result: {{ monoData }}</p>
    </div>

    <div class="section">
      <h2>Flux (SSE) Example</h2>
      <button @click="startStream" :disabled="listening">Start Stream</button>
      <button @click="stopStream" :disabled="!listening">Stop Stream</button>
      <ul class="stream-list">
        <li v-for="(event, index) in streamEvents" :key="index">{{ event }}</li>
      </ul>
    </div>

    <div class="section">
      <h2>Flux Operators Examples</h2>
      
      <div class="operator-group">
        <h3>Creation</h3>
        <button @click="runOperator('just')">Just</button>
        <button @click="runOperator('range')">Range</button>
        <button @click="runOperator('fromIterable')">FromIterable</button>
      </div>

      <div class="operator-group">
        <h3>Transformation</h3>
        <button @click="runOperator('map')">Map</button>
        <button @click="runOperator('flatMap')">FlatMap (Async)</button>
      </div>

      <div class="operator-group">
        <h3>Filtering</h3>
        <button @click="runOperator('filter')">Filter</button>
        <button @click="runOperator('take')">Take</button>
      </div>

      <div class="operator-group">
        <h3>Combination</h3>
        <button @click="runOperator('zip')">Zip</button>
        <button @click="runOperator('merge')">Merge</button>
      </div>

      <div class="operator-group">
        <h3>Error Handling</h3>
        <button @click="runOperator('error')">OnErrorReturn</button>
      </div>

      <div v-if="operatorResults.length > 0" class="results-area">
        <h4>Operator Results:</h4>
        <ul class="stream-list">
          <li v-for="(res, index) in operatorResults" :key="index">{{ res }}</li>
        </ul>
        <button @click="clearResults" class="clear-btn">Clear Results</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'WebFluxDemo',
  data() {
    return {
      monoData: '',
      streamEvents: [],
      listening: false,
      eventSource: null,
      operatorResults: [],
      currentOperatorSource: null
    }
  },
  methods: {
    async fetchMono() {
      try {
        const response = await fetch('/api/webflux/mono');
        this.monoData = await response.text();
      } catch (error) {
        console.error('Error fetching Mono:', error);
        this.monoData = 'Error fetching data';
      }
    },
    startStream() {
      if (this.listening) return;

      this.streamEvents = [];
      this.listening = true;
      
      // Use EventSource for Server-Sent Events
      this.eventSource = new EventSource('/api/webflux/stream');
      
      this.eventSource.onmessage = (event) => {
        this.streamEvents.push(event.data);
        // Keep only last 10 events
        if (this.streamEvents.length > 10) {
          this.streamEvents.shift();
        }
      };

      this.eventSource.onerror = (error) => {
        console.error('EventSource failed:', error);
        this.stopStream();
      };
    },
    stopStream() {
      if (this.eventSource) {
        this.eventSource.close();
        this.eventSource = null;
      }
      this.listening = false;
    },
    runOperator(type) {
      if (this.currentOperatorSource) {
        this.currentOperatorSource.close();
      }
      
      this.operatorResults = [];
      this.currentOperatorSource = new EventSource(`/api/webflux/flux/${type}`);
      
      this.currentOperatorSource.onmessage = (event) => {
        this.operatorResults.push(event.data);
      };

      this.currentOperatorSource.onerror = (event) => {
          // It's normal for the stream to end, so we just close it
          // In a real app, you might want to distinguish between completion and actual errors
          if (this.currentOperatorSource) {
             this.currentOperatorSource.close();
             this.currentOperatorSource = null;
          }
      };
    },
    clearResults() {
      this.operatorResults = [];
      if (this.currentOperatorSource) {
        this.currentOperatorSource.close();
        this.currentOperatorSource = null;
      }
    }
  },
  beforeUnmount() {
    this.stopStream();
    if (this.currentOperatorSource) {
      this.currentOperatorSource.close();
    }
  }
}
</script>

<style scoped>
.webflux-demo {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
}

.section {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

button {
  padding: 8px 16px;
  margin-right: 10px;
  background-color: #42b883;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

button:disabled {
  background-color: #a8d5c2;
  cursor: not-allowed;
}

.stream-list {
  list-style-type: none;
  padding: 0;
  margin-top: 15px;
  background-color: #f5f5f5;
  border-radius: 4px;
  max-height: 300px;
  overflow-y: auto;
}

.stream-list li {
  padding: 8px 12px;
  border-bottom: 1px solid #eee;
  font-family: monospace;
}

.stream-list li:last-child {
  border-bottom: none;
  background-color: #e6fffa; /* Highlight newest item */
}

.operator-group {
  margin-bottom: 15px;
}

.operator-group h3 {
  font-size: 1rem;
  margin-bottom: 5px;
  color: #666;
}

.results-area {
  margin-top: 20px;
  border-top: 2px dashed #eee;
  padding-top: 10px;
}

.clear-btn {
  background-color: #e74c3c;
  margin-top: 10px;
}
</style>
