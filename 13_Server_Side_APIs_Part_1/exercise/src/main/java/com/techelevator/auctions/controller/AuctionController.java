package com.techelevator.auctions.controller;

import com.techelevator.auctions.dao.AuctionDao;
import com.techelevator.auctions.dao.MemoryAuctionDao;
import com.techelevator.auctions.model.Auction;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AuctionController {

    private AuctionDao auctionDao;

    public AuctionController() {
        this.auctionDao = new MemoryAuctionDao();
    }

    //get  list of all Auctions + added search by title
    @RequestMapping(path = "/auctions", method = RequestMethod.GET)
    public List<Auction> list(@RequestParam(name =  "title_like", defaultValue = "") String titleLike,
                              @RequestParam(name = "currentBid_lte", defaultValue = "0") double currentBidLte) {
        //search by both title and bid
        if (!titleLike.isEmpty() && currentBidLte > 0) return auctionDao.getAuctionsByTitleAndMaxBid(titleLike, currentBidLte);
        //search by title only
        else if (!titleLike.isEmpty()) return auctionDao.getAuctionsByTitle(titleLike);
        //search by bid only
        else if (currentBidLte > 0) return auctionDao.getAuctionsByMaxBid(currentBidLte);
        //or search all
        else return auctionDao.getAuctions();
    }

    //get Auctions by id
    @RequestMapping(path = "/auctions/{id}", method = RequestMethod.GET)
    public Auction get(@PathVariable int id) {
        return auctionDao.getAuctionById(id);
    }

    //add auction
    @RequestMapping(path = "/auctions", method = RequestMethod.POST)
    public Auction create(@RequestBody Auction auction) {
        return auctionDao.createAuction(auction);
    }

}
