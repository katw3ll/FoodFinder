package com.example.foodfinder.Models;

public class VersionApi {

    int versionNumber;
    int releaseNumber;

    public VersionApi(int versionNumber, int releaseNumber) {
        this.versionNumber = versionNumber;
        this.releaseNumber = releaseNumber;
    }

    public boolean equals(Object obj)
    {
        VersionApi version = (VersionApi) obj;
        return this.releaseNumber == version.releaseNumber || this.versionNumber == version.versionNumber;
    }

}
