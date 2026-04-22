public class InfoSys {

    private Long id;
    private String firstName;
    private String lastName;
    private String street;
    private String city;

    public InfoSys(Long id, String firstName, String lastName, String street, String city) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.street = street;
        this.city = city;
    }

    public String getFirstName() {return firstName;}
    public void setFirstName(String firstName) {this.firstName = firstName;}
}
    

