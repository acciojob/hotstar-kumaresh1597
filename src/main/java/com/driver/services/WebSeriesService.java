package com.driver.services;

import com.driver.EntryDto.WebSeriesEntryDto;
import com.driver.model.ProductionHouse;
import com.driver.model.WebSeries;
import com.driver.repository.ProductionHouseRepository;
import com.driver.repository.WebSeriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebSeriesService {

    @Autowired
    WebSeriesRepository webSeriesRepository;

    @Autowired
    ProductionHouseRepository productionHouseRepository;

    public Integer addWebSeries(WebSeriesEntryDto webSeriesEntryDto)throws  Exception{

        //Add a webSeries to the database and update the ratings of the productionHouse
        //Incase the seriesName is already present in the Db throw Exception("Series is already present")
        //use function written in Repository Layer for the same
        //Dont forget to save the production and webseries Repo

        WebSeries webSeries = webSeriesRepository.findBySeriesName(webSeriesEntryDto.getSeriesName());
        if(webSeries != null) throw new Exception("Series is already present");

        ProductionHouse productionHouse = productionHouseRepository.findById(webSeriesEntryDto.getProductionHouseId()).get();

        webSeries = new WebSeries();
        webSeries.setSeriesName(webSeriesEntryDto.getSeriesName());
        webSeries.setAgeLimit(webSeriesEntryDto.getAgeLimit());
        webSeries.setSubscriptionType(webSeriesEntryDto.getSubscriptionType());
        webSeries.setRating(webSeriesEntryDto.getRating());
        webSeries.setProductionHouse(productionHouse);

        webSeries = webSeriesRepository.save(webSeries);

        productionHouse.getWebSeriesList().add(webSeries);
        double houseRatings = calculateRatings(productionHouse);
        productionHouse.setRatings(houseRatings);

        productionHouseRepository.save(productionHouse);

        return webSeries.getId();
    }

    private double calculateRatings(ProductionHouse productionHouse) {
        List<WebSeries> webSeriesList = productionHouse.getWebSeriesList();
        double total = 0.0;
        for(WebSeries webSeries : webSeriesList){
            total += webSeries.getRating();
        }
        return (double)total/webSeriesList.size();
    }

}
