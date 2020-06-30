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

package com.google.sps.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private List<String> messages;
  private List<String> comments = new ArrayList<String>();
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    messages = new ArrayList<String>();
    messages.add("It\'s a beautiful day in the neighborhood!");
    messages.add("Let\'s make the most of this beautiful day!");
    messages.add("I\'ve always wanted to have a neighbor just like you!");
    
    Query query = new Query("comments").addSort("timestamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    
    int numComments = Integer.parseInt(request.getParameter("numComments"));
    //Prints out everything in datastore
    comments.clear();
    for (Entity entity : results.asIterable()) {
        String comment = (String) entity.getProperty("comment");
        comments.add(comment);
        if (comments.size() == numComments) {
            break;
        }
    }

    String json = convertToJson(comments);
    response.setContentType("application/json");
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
      //Get the input from the form
      String text = getParameter(request, "text-input", "This is an empty comment!");
      long timestamp = System.currentTimeMillis();

      if (!text.equals("This is an empty comment!")) {
        comments.add(text);
      }
    
      //Creates Entity to store in Datastore
      Entity commentEntity = new Entity("comments");
      commentEntity.setProperty("comment", text);
      commentEntity.setProperty("timestamp", timestamp);


      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(commentEntity);

      response.setContentType("application/json");
      response.getWriter().println(comments);
      
      //Redirect back to the HTML page.
      response.sendRedirect("/index.html");
  }

  //Converts an array of strings into a Json array
  private String convertToJson(List<String> messages) {
    Gson gson = new Gson();
    String json = gson.toJson(messages);
    return json;
  }

  //retrieves the inputted comments on the website
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
      String value = request.getParameter(name);
      if (value == null) {
          return defaultValue;
      }
      return value;
  }
}


