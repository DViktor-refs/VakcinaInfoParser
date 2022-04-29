package com.company.vinfo;

import java.io.Serializable;

public class Deceased implements Serializable {

    private int no;
    private String sex;
    private int age;
    private String I1,I2,I3,I4,I5,I6;

    private Deceased(DeceasedBuilder builder) {
        this.no = builder.no;
        this.sex = builder.sex;
        this.age = builder.age;
        this.I1 = builder.I1;
        this.I2 = builder.I2;
        this.I3 = builder.I3;
        this.I4 = builder.I4;
        this.I5 = builder.I5;
        this.I6 = builder.I6;
    }

    public int getNo() {
        return no;
    }

    public String getSex() {
        return sex;
    }

    public int getAge() {
        return age;
    }

    public String getI1() {
        return I1;
    }

    public String getI2() {
        return I2;
    }

    public String getI3() {
        return I3;
    }

    public String getI4() {
        return I4;
    }

    public String getI5() {
        return I5;
    }

    public String getI6() {
        return I6;
    }

    @Override
    public String toString() {
        return "Deceased{" +
                "no=" + no +
                ", sex='" + sex + '\'' +
                ", age=" + age +
                ", I1='" + I1 + '\'' +
                ", I2='" + I2 + '\'' +
                ", I3='" + I3 + '\'' +
                ", I4='" + I4 + '\'' +
                ", I5='" + I5 + '\'' +
                ", I6='" + I6 + '\'' +
                '}';
    }

    public static class DeceasedBuilder {

        private int no;
        private String sex;
        private int age;
        private String I1,I2,I3,I4,I5,I6;

        public DeceasedBuilder setNo(int no) {
            this.no = no;
            return this;
        }

        public DeceasedBuilder setSex (String sex) {
            this.sex = sex;
            return this;
        }

        public DeceasedBuilder setAge(int age) {
            this.age = age;
            return this;
        }

        public DeceasedBuilder setI1(String I1) {
            this.I1 = I1;
            return this;
        }

        public DeceasedBuilder setI2(String I2) {
            this.I2 = I2;
            return this;
        }

        public DeceasedBuilder setI3(String I3) {
            this.I3 = I3;
            return this;
        }

        public DeceasedBuilder setI4(String I4) {
            this.I4 = I4;
            return this;
        }

        public DeceasedBuilder setI5(String I5) {
            this.I5 = I5;
            return this;
        }

        public DeceasedBuilder setI6(String I6) {
            this.I6 = I6;
            return this;
        }

        public Deceased build() {
            return new Deceased(this);
        }
    }
}
