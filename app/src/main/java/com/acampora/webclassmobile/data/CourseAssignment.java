/*
 * Created by Jonathan Acampora 2016
 * Copyright (c) 2016. All rights reserved
 *
 * Last modified 10/6/16 12:55 PM
 */

package com.acampora.webclassmobile.data;

/**
 * Created by Redd on 10/4/2016.
 */

public class CourseAssignment extends MoodleData {
    public Integer id;
    public Integer cmid;
    public Integer course;
    public String name;
    public Integer nosubmissions;
    public Integer submissiondrafts;
    public Integer sendnotifications;
    public Integer sendlatenotifications;
    public Integer sendstudentnotifications;
    public Long duedate;
    public Integer allowsubmissionsfromdate;
    public Integer grade;
    public Long timemodified;
    public Integer completionsubmit;
    public Integer cutoffdate;
    public Integer teamsubmission;
    public Integer requireallteammemberssubmit;
    public Integer teamsubmissiongroupingid;
    public Integer blindmarking;
    public Integer revealidentities;
    public String attemptreopenmethod;
    public Integer maxattempts;
    public Integer markingworkflow;
    public Integer markingallocation;
    public Integer requiresubmissionstatement;

    public AssignmentConfiguration[] configs;

    public String intro;
    public Integer introformat;

}
