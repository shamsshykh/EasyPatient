package com.app.easy_patient.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class LoginResponseModel implements Parcelable{
    private String refresh_token;
    private String access_token;
    private String registered;
    @SerializedName("user")
    @Expose
    private User user;

    protected LoginResponseModel(Parcel in) {
        refresh_token = in.readString();
        access_token = in.readString();
        registered = in.readString();
        user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<LoginResponseModel> CREATOR = new Creator<LoginResponseModel>() {
        @Override
        public LoginResponseModel createFromParcel(Parcel in) {
            return new LoginResponseModel(in);
        }

        @Override
        public LoginResponseModel[] newArray(int size) {
            return new LoginResponseModel[size];
        }
    };

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }



    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }



    public ArrayList<Object> getNon_field_errors() {
        return non_field_errors;
    }

    public void setNon_field_errors(ArrayList<Object> non_field_errors) {
        this.non_field_errors = non_field_errors;
    }

    ArrayList< Object > non_field_errors = new ArrayList < Object > ();

    // Getter Methods

    public String getRegistered() {
        return registered;
    }

    // Setter Methods

    public void setRegistered(String registered) {
        this.registered = registered;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(refresh_token);
        dest.writeString(access_token);
        dest.writeString(registered);
        dest.writeParcelable(user, flags);
    }

    public class User implements Parcelable {

        @SerializedName("username")
        @Expose
        private String username;
        @SerializedName("language")
        @Expose
        private String language;
        @SerializedName("full_name")
        @Expose
        private String fullName;
        @SerializedName("birth_date")
        @Expose
        private String birthDate;
        @SerializedName("profile_pic")
        @Expose
        private String profilePic;
        @SerializedName("gender")
        @Expose
        private String gender;
        @SerializedName("is_active")
        @Expose
        private String is_active;

        protected User(Parcel in) {
            username = in.readString();
            language = in.readString();
            fullName = in.readString();
            birthDate = in.readString();
            profilePic = in.readString();
            gender = in.readString();
            is_active = in.readString();
        }

        public final Creator<User> CREATOR = new Creator<User>() {
            @Override
            public User createFromParcel(Parcel in) {
                return new User(in);
            }

            @Override
            public User[] newArray(int size) {
                return new User[size];
            }
        };

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getBirthDate() {
            return birthDate;
        }

        public void setBirthDate(String birthDate) {
            this.birthDate = birthDate;
        }

        public String getProfilePic() {
            return profilePic;
        }

        public void setProfilePic(String profilePic) {
            this.profilePic = profilePic;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }

        public String getIsActive() {
            return is_active;
        }

        public void setIsActive(String isActive) {
            this.is_active = isActive;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(username);
            dest.writeString(language);
            dest.writeString(fullName);
            dest.writeString(birthDate);
            dest.writeString(profilePic);
            dest.writeString(gender);
            dest.writeString(is_active);
        }
    }
}
