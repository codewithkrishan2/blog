package com.kksg.blog.utils;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.kksg.blog.services.NotificationService;

@Component
public class ContentSanitizer {

	private final NotificationService emailNotificationService;
	
//	private static final Set<String> BANNED_WORDS = Set.of(
//		    // Profanity/Sexual Content
//		    "fuck", "shit", "bitch", "asshole", "cock", "pussy", "dick", "nude", "porn", "whore", "slut", "cum",
//		    // Racial/Ethnic Slurs
//		    "nigger", "chink", "spic", "kike", "gook", "paki", "wop", "cracker", "gyp", "sandnigger", "gypsy", 
//		    // Hate Speech
//		    "kill yourself", "die", "terrorist", "rape", "rape me", "bomb", "jihad", "suicide", "genocide", 
//		    // Violence and Threats
//		    "stab", "shoot", "beat up", "murder", "kill", "kill you", "gun", "assault", "violence", "slaughter", 
//		    // Drug & Alcohol Abuse
//		    "crack", "heroin", "meth", "weed", "cocaine", "marijuana", "drunk", "alcoholic", "addict", 
//		    // Abusive or Harassing Language
//		    "ugly", "stupid", "retard", "fat", "loser", "worthless", "idiot", "moron", "bastard",  "faggot",
//		    // Sexual Orientation and Gender Identity
//		    "dyke", "queer", "tranny", "shemale",
//		    // Sexual Content and Exploitation
//		    "molest", "pedophile", "paedophile", "child pornography", "shag",
//		    // Religious Hate Speech
//		    "blasphemy", "godless", "infidel", "devil worshipper", "satanic",
//		    // Drug Abuse and Addiction
//		    "crackhead", "junkie", "heroin addict", "methhead", "stoner", "weedhead"
//		);


    public ContentSanitizer(NotificationService emailNotificationService) {
    	this.emailNotificationService = emailNotificationService;
    }
  
    @Async
    public String sanitizeContent(String input) {
        String sanitizedContent = Jsoup.clean(input, Safelist.basic()); // Safelist.basic() allows only basic HTML tags
        if (containsProfanity(sanitizedContent)) {
            emailNotificationService.sendOffensiveContentNotification(sanitizedContent);
        }

        return sanitizedContent;
    }

    @Async
    private boolean containsProfanity(String text) {
//        for (String word : BANNED_WORDS) {
//            if (text.toLowerCase().contains(word)) {
//                return true;
//            }
//        }
        return false;
    }
    
}