package com.techelevator.auctions.services;

import org.springframework.web.client.RestTemplate;

import com.techelevator.auctions.model.Auction;

public class AuctionService {

    public static String API_BASE_URL = "http://localhost:3000/auctions/";
    private RestTemplate restTemplate = new RestTemplate();


    public Auction[] getAllAuctions() {
        return restTemplate.getForObject(API_BASE_URL, Auction[].class);
    }

    public Auction getAuction(int id) {
        return restTemplate.getForObject(API_BASE_URL + id, Auction.class);
    }

    public Auction[] getAuctionsMatchingTitle(String title) {
        return restTemplate.getForObject(API_BASE_URL + "?title_like=" + title, Auction[].class);
    }

    public Auction[] getAuctionsAtOrBelowPrice(double price) {
        return restTemplate.getForObject(API_BASE_URL + "?currentBid_lte=" + price, Auction[].class);
    }

}
