package com.internportal.model;

public class Skill {
    private int id;
    private String skillName;
    private String category;
    private String proficiencyLevel; // For student_skills: 'Beginner', 'Intermediate', 'Advanced'
    private boolean isMandatory;     // For internship_skills: true if mandatory

    public Skill() {
    }

    public Skill(int id, String skillName, String category) {
        this.id = id;
        this.skillName = skillName;
        this.category = category;
    }

    public Skill(int id, String skillName, String category, String proficiencyLevel) {
        this.id = id;
        this.skillName = skillName;
        this.category = category;
        this.proficiencyLevel = proficiencyLevel;
    }

    public Skill(int id, String skillName, String category, boolean isMandatory) {
        this.id = id;
        this.skillName = skillName;
        this.category = category;
        this.isMandatory = isMandatory;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProficiencyLevel() {
        return proficiencyLevel;
    }

    public void setProficiencyLevel(String proficiencyLevel) {
        this.proficiencyLevel = proficiencyLevel;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public void setMandatory(boolean mandatory) {
        isMandatory = mandatory;
    }

    @Override
    public String toString() {
        return "Skill{" +
                "id=" + id +
                ", skillName='" + skillName + '\'' +
                ", category='" + category + '\'' +
                ", proficiencyLevel='" + proficiencyLevel + '\'' +
                ", isMandatory=" + isMandatory +
                '}';
    }
}

