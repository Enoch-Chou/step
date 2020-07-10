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

google.charts.load('current', {'packages':['corechart']});
google.charts.setOnLoadCallback(drawChart);

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

  //retrieves and formats the inputted comments from the website
function getComments() {
    const numberOfComments = document.getElementById('inputtedNumber').value;
    const query = `/data?numComments=${ numberOfComments }`;
    fetch(query).then(response => response.json()).then((comments) => {
        const commentList = document.getElementById('comment-container');
        commentList.innerHTML = ''
        for (index = 0; index < comments.length; index++) {
            commentList.appendChild(createListElement(comments[index]));
        }
    });
}

function deleteComments() {
    fetch('/delete-data').then(response => response.json()).then((message) => {
        const taskComplete = document.getElementById('completionNotice');
        taskComplete.innerText = "";
    });
}


function createListElement(text) {
    const listElement = document.createElement('li');
    listElement.innerText = text;
    return listElement
    
}

//draws a chart based on user-submitted data
function drawChart() {
    fetch('/chocolate-data').then(response => response.json()).then((chocolateTypeVotes) => {
        const data = new google.visualization.DataTable();
        data.addColumn('string', 'Type of Chocolate');
        data.addColumn('number', 'Votes');
            Object.keys(chocolateTypeVotes).forEach((chocolateType) => {
                data.addRow([chocolateType, chocolateTypeVotes[chocolateType]]);
            });

        const options = {
            'title': 'Chocolate Preferences',
            is3D: true,
            'width':500,
            'height':400
        };

        const chart = new google.visualization.PieChart(
            document.getElementById('chart-container'));
        chart.draw(data, options);
    });
}

function createMap() {
    const map = new google.maps.Map(
        document.getElementById('map'),
        {center: {lat: 33.3014708, lng: -111.9006872}, zoom:15});

    const in_n_out_marker = new google.maps.Marker({
        position: {lat: 33.3064084, lng: -111.8917977},
        map: map,
        title: 'In-n-out burger'
    });

    const chandler_fashion_center_marker = new google.maps.Marker({
        position: {lat: 33.3014708, lng: -111.9006872},
        map: map,
        title: 'Chandler Fashion Center'
    });

    const chipotle_marker = new google.maps.Marker({
        position: {lat: 33.3051988, lng: -111.8999072},
        map: map,
        title: 'Chipotle Mexican Grill'
    });
    
    const in_n_out_info_window = 
        new google.maps.InfoWindow({content: 'This is in-n-out burger, one of my favorite fast food joints.'});
    in_n_out_info_window.open(map, in_n_out_marker);

    const chandler_fashion_center_info_window = 
        new google.maps.InfoWindow({content: 'This is the Chandler Fashion Center, a frequent hangout spot for me and my friends'});
    chandler_fashion_center_info_window.open(map, chandler_fashion_center_marker);

    const chipotle_info_window = 
        new google.maps.InfoWindow({content: 'This is Chipotle, another one of my favorite fast food joints'});
    chipotle_info_window.open(map, chipotle_marker);
}