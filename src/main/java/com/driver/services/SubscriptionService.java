package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        User user = userRepository.findById(subscriptionEntryDto.getUserId()).get();
        Subscription subscription = new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        if(subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.BASIC)){
            subscription.setTotalAmountPaid(500+(200 * subscriptionEntryDto.getNoOfScreensRequired()));
        } else if (subscriptionEntryDto.getSubscriptionType().equals(SubscriptionType.PRO)) {
            subscription.setTotalAmountPaid(800 + (250 * subscriptionEntryDto.getNoOfScreensRequired()));
        } else {
            subscription.setTotalAmountPaid(1000 + (350 * subscriptionEntryDto.getNoOfScreensRequired()));
        }
        subscription.setStartSubscriptionDate(new Date());
        subscription.setUser(user);

        //subscription = subscriptionRepository.save(subscription);

        user.setSubscription(subscription);
        userRepository.save(user);

        return subscription.getTotalAmountPaid();
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository

        User user = userRepository.findById(userId).get();
        if(user.getSubscription().equals(SubscriptionType.ELITE)){
            throw new Exception("Already the best Subscription");
        }
        Subscription subscription = user.getSubscription();
        int priceDifference = 0;
        if(subscription.getSubscriptionType().equals(SubscriptionType.BASIC)){
            int proPrice = 800 + (250 * subscription.getNoOfScreensSubscribed());
            subscription.setSubscriptionType(SubscriptionType.PRO);
            priceDifference = proPrice - subscription.getTotalAmountPaid();
            subscription.setTotalAmountPaid(proPrice);
        } else if (subscription.getSubscriptionType().equals(SubscriptionType.PRO)){
            int elitePrice = 1000 + (350 * subscription.getNoOfScreensSubscribed());
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            priceDifference = elitePrice - subscription.getTotalAmountPaid();
            subscription.setTotalAmountPaid(elitePrice);
        }

        subscriptionRepository.save(subscription);

        return priceDifference;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb

        List<Subscription> subscriptionList = subscriptionRepository.findAll();
        int totalRevenue = 0;
        for(Subscription subscription : subscriptionList){
            totalRevenue += subscription.getTotalAmountPaid();
        }
        return totalRevenue;
    }

}
