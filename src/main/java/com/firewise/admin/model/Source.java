package com.firewise.admin.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sources")
public class Source {

    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    private String url;
    private String address;
    private String phone;

    @Column(length = 2000)
    private String notes;

    private String region;

    @Column(name = "target_location")
    private String targetLocation;

    @Column(name = "topics_addressed", length = 1000)
    private String topicsAddressed;

    @Column(length = 2000)
    private String attribution;

    @Column(name = "ref_code")
    private String refCode;

    @Column(name = "file_link")
    private String fileLink;

    public Source() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }

    public String getTargetLocation() { return targetLocation; }
    public void setTargetLocation(String targetLocation) { this.targetLocation = targetLocation; }

    public String getTopicsAddressed() { return topicsAddressed; }
    public void setTopicsAddressed(String topicsAddressed) { this.topicsAddressed = topicsAddressed; }

    public String getAttribution() { return attribution; }
    public void setAttribution(String attribution) { this.attribution = attribution; }

    public String getRefCode() { return refCode; }
    public void setRefCode(String refCode) { this.refCode = refCode; }

    public String getFileLink() { return fileLink; }
    public void setFileLink(String fileLink) { this.fileLink = fileLink; }
}
