// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * Adds a random fact about me to the page.
 */
function addRandomFact() {
  const facts =
      ['I was born in Arizona', 'I play three instruments', 'My favorite animal is the capybara', 'I\'ve never broken a bone before'];

  // Pick a random fact.
  const fact = facts[Math.floor(Math.random() * facts.length)];

  // Add it to the page.
  const factContainer = document.getElementById('fact-container');
  factContainer.innerText = fact;
}

function getMessage() {
    fetch('/data')
    .then(response => response.json()).then((messages) => {

        const messageList = document.getElementById('message-container');
        messageList.innerHTML = ''
        messageList.appendChild(createListElement('Message 1: ' + messages[0]));
        messageList.appendChild(createListElement('Message 2: ' + messages[1]));
        messageList.appendChild(createListElement('Message 3: ' + messages[2]));
    });


}

function createListElement(text) {
    const listElement = document.createElement('li');
    listElement.innerText = text;
    return listElement
    
}