package eu.michalkijowski.carvisor.data_models;

public class UsersDTO {
    int page;
    int pageMax;
    UserDTO[] listOfUsers;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageMax() {
        return pageMax;
    }

    public void setPageMax(int pageMax) {
        this.pageMax = pageMax;
    }

    public UserDTO[] getListOfUsers() {
        return listOfUsers;
    }

    public void setListOfUsers(UserDTO[] listOfUsers) {
        this.listOfUsers = listOfUsers;
    }
}