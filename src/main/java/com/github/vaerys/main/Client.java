package com.github.vaerys.main;

import com.github.vaerys.objects.EventAvatar;
import com.github.vaerys.objects.PatreonAPI;
import com.github.vaerys.objects.TimedEvent;
import com.patreon.resources.Campaign;
import com.patreon.resources.Pledge;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.Image;

import java.io.File;
import java.io.IOException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vaerys on 19/05/2016.
 */
public class Client {
    final static Logger logger = LoggerFactory.getLogger(Client.class);
    private static PatreonAPI patreonApi = null;
    private static IDiscordClient client = null;

    public static IDiscordClient createClient(String token, boolean login) throws DiscordException {
        ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.withToken(token);
        clientBuilder.setMaxReconnectAttempts(4000);
        if (login) {
            logger.info("Logging in to Discord");
            client = clientBuilder.login();
        } else {
            client = clientBuilder.build();
        }
        return client;
    }

    public static IDiscordClient getClient() {
        return client;
    }


    public static void main(String[] args) {
        IDiscordClient client = new ClientBuilder().withToken(args[0]).build();
        client.getDispatcher().registerListener((IListener<ReadyEvent>) readyEvent -> System.out.println("login successful"));
        client.login();
    }


    public static PatreonAPI initPatreon(List<String> token) throws IndexOutOfBoundsException {
        //------------------------------------------client id, client secret, redirect uri
        //PatreonOAuth patreonOAuth = new PatreonOAuth(token.get(0),token.get(1),token.get(2));

        patreonApi = new PatreonAPI(token.get(0));
        logger.info("Patreon Account Linked.");

        checkPatrons();
        return patreonApi;
    }

    public static PatreonAPI getPatreonApi() {
        return patreonApi;
    }

    public static void checkPatrons() {
        List<Long> patronIDs = new ArrayList<>();
        try {
            PatreonAPI patreonAPI = getPatreonApi();
            if (patreonAPI == null) return;
            List<Campaign> campaigns = patreonAPI.fetchCampaigns().get();
            for (Campaign c : campaigns) {
                List<Pledge> pledges = patreonAPI.fetchAllPledges(c.getId());
                if (pledges != null) {
                    for (Pledge p : pledges) {
                        if (p.getReward().getTitle().equalsIgnoreCase("Pioneer")) {
                            try {
                                long userID = Long.parseUnsignedLong(p.getPatron().getSocialConnections().getDiscord().getUser_id());
                                patronIDs.add(userID);
                            } catch (NumberFormatException e) {
                                //skip
                            }
                        }
                    }
                }
            }
            logger.info("Patron List Updated.");
        } catch (IOException e) {
            //nothing happens
        }
        Globals.setPatrons(patronIDs);
    }

    public static void handleAvatars() {
        TimedEvent event = Globals.getCurrentEvent();
        ZonedDateTime timeNow = ZonedDateTime.now(ZoneOffset.UTC);
        String dailyFileName = Globals.dailyAvatarName.replace("#day#", timeNow.getDayOfWeek().toString());
        File avatarFile;

        //sets Avatar.
        if (Globals.doDailyAvatars) {
            avatarFile = new File(Constants.DIRECTORY_GLOBAL_IMAGES + dailyFileName);
        } else {
            avatarFile = new File(Constants.DIRECTORY_GLOBAL_IMAGES + Globals.defaultAvatarFile);
        }
        Image avatar = Image.forFile(avatarFile);
        if (event != null && event.doRotateAvatars()) {
            EventAvatar eventAvatar = event.getAvatarDay(timeNow.getDayOfWeek());
            if (eventAvatar != null) {
                avatar = Image.forUrl(FilenameUtils.getExtension(eventAvatar.getLink()), eventAvatar.getLink());
            }
        }
        Utility.updateAvatar(avatar);
        //wait for the avatar to update properly
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Utility.sendStack(e);
        }
    }
}