/*
 * Copyright 2016 Pivotal Software, Inc..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.proxyprint.kitchen.controllers.printshops;

import com.google.gson.Gson;
import io.github.proxyprint.kitchen.models.consumer.Consumer;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import io.github.proxyprint.kitchen.models.printshops.Review;
import io.github.proxyprint.kitchen.models.repositories.ConsumerDAO;
import io.github.proxyprint.kitchen.models.repositories.PrintShopDAO;
import io.github.proxyprint.kitchen.models.repositories.ReviewDAO;
import io.swagger.annotations.ApiOperation;
import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

/**
 *
 * @author jose
 */
@RestController
public class ReviewController {

    @Autowired
    private ConsumerDAO consumers;
    @Autowired
    private PrintShopDAO printshops;
    @Autowired
    private ReviewDAO reviews;
    @Autowired
    private Gson GSON;

    @ApiOperation(value = "Returns printshop reviews", notes = "Returns the reviews of a printshop.")
    @Secured({"ROLE_MANAGER", "ROLE_EMPLOYEE", "ROLE_USER"})
    @RequestMapping(value = "/printshops/{id}/reviews", method = RequestMethod.GET)
    public ResponseEntity<String> getPrintShopReviews(@PathVariable("id") long id) {
        PrintShop pShop = this.printshops.findOne(id);
        if (pShop == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity(this.GSON.toJson(pShop.getReviews()), HttpStatus.OK);
        }
    }

    @ApiOperation(value = "Add a printshop review", notes = "Add a review to the printshop with the given ID")
    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/printshops/{id}/reviews", method = RequestMethod.POST)
    public ResponseEntity<String> addPrintShopReview(@PathVariable("id") long id, Principal principal, WebRequest request) {
        PrintShop pShop = this.printshops.findOne(id);
        if (pShop == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        Consumer consumer = this.consumers.findByUsername(principal.getName());
        String reviewText = request.getParameter("review");
        int rating = Integer.parseInt(request.getParameter("rating"));
        Review review = reviews.save(new Review(reviewText, rating, consumer));
        pShop.addReview(review);
        this.printshops.save(pShop);
        return new ResponseEntity(this.GSON.toJson(review), HttpStatus.OK);
    }
}
