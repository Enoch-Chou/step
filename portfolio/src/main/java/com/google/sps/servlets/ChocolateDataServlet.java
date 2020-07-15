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

import com.google.gson.Gson;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/chocolate-data")
public class ChocolateDataServlet extends HttpServlet {
    
    private Map<String, Integer> chocolateTypeVotes = new HashMap<>();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        Gson gson = new Gson();
        String json = gson.toJson(chocolateTypeVotes);
        response.getWriter().println(json);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String chocolateType = request.getParameter("chocolateType");
        int currentVotes = chocolateTypeVotes.containsKey(chocolateType) ? chocolateTypeVotes.get(chocolateType) : 0;
        chocolateTypeVotes.put(chocolateType, currentVotes + 1);
        
        Entity chocolateEntity = new Entity("chocolateTypes");
        chocolateEntity.setProperty("chocolate", chocolateType);

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        datastore.put(chocolateEntity);

        response.sendRedirect("/explore.html");
    }
}