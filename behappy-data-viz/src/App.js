import React, { Component } from 'react';
import logo from './images/happy_icon.png';
import './App.css';

class App extends Component {
  render() {
    return (
      <div className="App">
        <div className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <h2>BeHappy</h2>
        </div>
      </div>
    );
  }
}

export default App;
