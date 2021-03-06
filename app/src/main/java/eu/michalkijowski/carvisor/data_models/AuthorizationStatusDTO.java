package eu.michalkijowski.carvisor.data_models;

public class AuthorizationStatusDTO {
    String rbac;
    String nickname;
    boolean logged;
    int id;

    public String getRbac() {
        return rbac;
    }

    public void setRbac(String rbac) {
        this.rbac = rbac;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public boolean isLogged() {
        return logged;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}