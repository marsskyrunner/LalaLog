package com.mars_skyrunner.lalalog;


/*Creates an object called Subject with the information of the author who writes Records
* Each subject will be part of a group of subjects.
* */

public class Subject {

    private String mSubjectID , mSubjectName, mSubjectLastName1, mSubjectLastName2,mSubjectBirthdate, mGroupID;
    private int mGroupResourceID = 0 ;
    private int subjectGroup;
    private int mSubjectUniqueID;


    public Subject(String name, String lastName1, String lastName2, String birthDate, String groupID){
        mSubjectName = name;
        mSubjectLastName1 = lastName1;
        mSubjectLastName2 = lastName2;
        mSubjectBirthdate = birthDate;
        mGroupID = groupID;
    }

    public String getSubjectID() {
        return mSubjectID;
    }

    public void setSubjectID(String mSubjectID) {
        this.mSubjectID = mSubjectID;
    }

    public Subject(String subjectID, String name, String lastName1, String lastName2, String birthDate, String groupID){
        mSubjectID = subjectID;
        mSubjectName = name;
        mSubjectLastName1 = lastName1;
        mSubjectLastName2 = lastName2;
        mSubjectBirthdate = birthDate;
        mGroupID = groupID;
    }


    public String getSubjectName() {
        return mSubjectName;
    }

    public void setSubjectName(String mSubjectName) {
        this.mSubjectName = mSubjectName;
    }

    public String getSubjectLastName1() {
        return mSubjectLastName1;
    }

    public void setSubjectLastName1(String mSubjectLastName1) {
        this.mSubjectLastName1 = mSubjectLastName1;
    }

    public String getSubjectLastName2() {
        return mSubjectLastName2;
    }

    public void setSubjectLastName2(String mSubjectLastName2) {
        this.mSubjectLastName2 = mSubjectLastName2;
    }

    public String getSubjectBirthdate() {
        return mSubjectBirthdate;
    }

    public void setSubjectBirthdate(String mSubjectBirthdate) {
        this.mSubjectBirthdate = mSubjectBirthdate;
    }

    public String getGroupID() {

        return mGroupID;
    }

    public String getGroupDisplay() {

        String groupID = "";

        switch (mGroupID){
            case "0":
                groupID = "A";
                break;

            case "1":
                groupID = "B";
                break;

            case "2":
                groupID = "C";
                break;
        }

        return groupID;
    }

    public void setGroupID(String mGroupID) {
        this.mGroupID = mGroupID;
    }

    public int getGroupResourceID(){
        int resourceID = 0;

        switch (mGroupID){
            case "0":
                resourceID = R.drawable.subject_group_1;
                break;

            case "1":
                resourceID = R.drawable.subject_group_2;
                break;

            case "2":
                resourceID = R.drawable.subject_group_3;
                break;
        }


        return  resourceID;
    }

    public void setSubjectUniqueID(int newUniqueID) {
        mSubjectUniqueID = newUniqueID;
    }

    public int getSubjectUniqueID() {
        return mSubjectUniqueID;
    }
}
