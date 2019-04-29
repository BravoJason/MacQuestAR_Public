package com.mcmaster.wiser.idyll.model;

/**
 * Created by steve on 2017-08-23.
 */

public class CheckNewVersion {

    public String latestVersion;

    public boolean changed;

    public String link;

    public String clientVersion;

    @Override
    public String toString() {
        return "CheckNewVersion{" +
                "latestVersion='" + latestVersion + '\'' +
                ", changed='" + changed + '\'' +
                ", link='" + link + '\'' +
                ", clientVersion='" + clientVersion + '\'' +
                '}';
    }
}
