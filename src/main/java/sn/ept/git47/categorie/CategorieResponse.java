package sn.ept.git47.categorie;

public class CategorieResponse {
    private Categorie categorie;
    private String msg;

    public CategorieResponse() { }

    public CategorieResponse(Categorie categorie) { this.categorie = categorie; }

    public CategorieResponse(Categorie categorie, String msg) {
        this.categorie = categorie;
        this.msg = msg;
    }

    public Categorie getCategorie() { return categorie; }

    public void setCategorie(Categorie categorie) { this.categorie = categorie; }

    public String getMsg() { return msg; }

    public void setMsg(String msg) { this.msg = msg; }
}
