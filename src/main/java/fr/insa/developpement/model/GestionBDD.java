package fr.insa.developpement.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.notification.Notification.Position;

import fr.insa.beuvron.utils.ConsoleFdB;
import fr.insa.beuvron.utils.exceptions.ExceptionsUtils;
import fr.insa.beuvron.utils.list.ListUtils;
import fr.insa.developpement.model.classes.Machine;
import fr.insa.developpement.model.classes.TypeOperation;
import fr.insa.developpement.model.classes.Utilisateur;

public class GestionBDD {

    private static GestionBDD instance;
    private Connection conn;

    private GestionBDD() {
        try {
            this.conn = connectGeneralMySQL(
                "92.222.25.165",
                3306,
                "m3_hgounon01",
                "m3_hgounon01",
                "ae2fe50b"
            );
        } catch (SQLException e) {
            Notification fatalError = Notification.show("Impossible de se connecter à la base de données. Une erreur est survenue : " + e.getLocalizedMessage());
            fatalError.addThemeVariants(NotificationVariant.LUMO_ERROR);
            fatalError.setDuration(10);
            fatalError.setPosition(Position.MIDDLE);
        }
    }

    // La manière dont GestionBDD a été modifiée permet de ne se connecter qu'une seule fois à la BDD
    // pour améliorer l'efficacité du projet.

    // Pour récupérer une connexion à la BDD, n'importe où dans le projet, en une ligne :
    // Connection connection = GestionBDD.getConnection();

    private static GestionBDD getInstance() {
        if (instance == null) {
            synchronized (GestionBDD.class) {
                if (instance == null) {
                    instance = new GestionBDD();
                }
            }
        }
        return instance;
    }

    public static Connection getConnection() {
        return GestionBDD.getInstance().conn;
    }

    private static Connection connectGeneralMySQL(String host,
            int port, String database,
            String user, String pass)
            throws SQLException {
        Connection con = DriverManager.getConnection(
                "jdbc:mysql://" + host + ":" + port
                + "/" + database,
                user, pass);
        con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        return con;
    }

    public static void creeSchema() throws SQLException {
        Connection conn = getConnection();
        conn.setAutoCommit(false);
        try (Statement st = conn.createStatement()) {
            st.executeUpdate(
                    "create table machine (\n"
                    + "    id integer not null primary key AUTO_INCREMENT,\n"
                    + "    ref varchar(30) not null unique,\n"
                    + "    des varchar(100) not null,\n"
                    + "    puissance double not null\n"   
                    + ")\n"
            );
            st.executeUpdate(
                    "create table typeoperation (\n"
                    + "    id integer not null primary key AUTO_INCREMENT,\n"
                    + "    nom varchar(100) not null unique,\n"
                    + "    des varchar(100) not null\n"
                    + ")\n"
            );
             st.executeUpdate(
                    "create table realise (\n"
                    + "    idMachine integer null unique,\n"
                    + "    idType integer null,\n"
                    + "    duree integer not null\n"        
                    + ")\n"
            );
            st.executeUpdate(
                "CREATE INDEX fk_machine_id ON realise (idMachine)"
            );
            st.executeUpdate(
                    "alter table realise \n"
                    + "    add constraint fk_machine_id \n"
                    + "    foreign key (idMachine) references machine(id) \n"
            );
            st.executeUpdate(
                    "alter table realise \n"
                    + "    add constraint fk_typeoperation_id \n"
                    + "    foreign key (idType) references typeoperation(id) \n"
            );
             st.executeUpdate(
                    "create table produit (\n"
                    + "    id integer not null primary key AUTO_INCREMENT,\n"
                    + "    ref varchar(30) not null unique,\n"
                    + "    des varchar(100) not null\n"  
                    + ")\n"
            );
            st.executeUpdate(
                    "create table operation (\n"
                    + "    id integer not null primary key AUTO_INCREMENT,\n"
                    + "    idType integer not null,\n"
                    + "    idProduit integer not null,\n"
                    + "    rang integer not null"
                    + ")\n"
            );
            st.executeUpdate(
                    "alter table operation \n"
                    + "    add constraint fk_typeoperation_idoperation \n"
                    + "    foreign key (idType) references typeoperation(id) \n"
            );
            st.executeUpdate(
                    "alter table operation \n"
                    + "    add constraint fk_produit_id \n"
                    + "    foreign key (idProduit) references produit(id) \n"
            );
            st.executeUpdate(
                    "create table commande (\n"
                    + "    id integer not null primary key AUTO_INCREMENT,\n"
                    + "    nomClient VARCHAR(100) not null,\n"
                    + "    dateCommande DATE not null\n"
                    + ")\n"
            );
            st.executeUpdate(
                    "create table produit_commande (\n"
                    + "    id integer not null primary key AUTO_INCREMENT,\n"
                    + "    idCommande integer not null,\n"
                    + "    idProduit integer not null,\n"
                    + "    quantite integer not null"
                    + ")\n"
            );
            st.executeUpdate(
                    "alter table produit_commande \n"
                    + "    add constraint fk_idCommande_produitCommande \n"
                    + "    foreign key (idCommande) references commande(id) \n"
            );
            st.executeUpdate(
                    "alter table produit_commande \n"
                    + "    add constraint fk_idProduit_produitCommande \n"
                    + "    foreign key (idProduit) references produit(id) \n"
            );
            conn.commit();
        } catch (SQLException ex) {
            conn.rollback();
            throw ex;
        } finally {
            conn.setAutoCommit(true);
        }
    }

    public static void deleteSchema() throws SQLException {
        Connection conn = getConnection();
        try (Statement st = conn.createStatement()) {
            try {
                st.executeUpdate("alter table realise drop constraint fk_machine_id");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("alter table realise drop constraint fk_typeoperation_id");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("alter table operation drop constraint fk_typeoperation_idoperation");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("alter table operation drop constraint fk_produit_id");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("alter table produit_commande drop constraint fk_idCommande_produitCommande");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("alter table produit_commande drop constraint fk_idProduit_produitCommande");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("drop table machine");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("drop table realise");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("drop table typeoperation");
            } catch (SQLException ex) {
            }
             try {
                st.executeUpdate("drop table produit");
            } catch (SQLException ex) {
            }
             try {
                st.executeUpdate("drop table operation");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("drop table produit_commande");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("drop table commande");
            } catch (SQLException ex) {
            }
        }
    }

    public static void initTest(){
        try {
            Machine newMachine = new Machine("Drill", "MCH001", 1500);
            newMachine.save();

            TypeOperation t1= new TypeOperation("Fraisage", "Enlèvement de matière");
            t1.addMachine(Machine.getMachineFromId(1));
            t1.save();

            Machine newMachine2 = new Machine("Lathe", "MCH002", 5000);
            newMachine2.save();

        } catch (SQLException exc) {
            System.out.println("ERREUR t1.save " + exc.getLocalizedMessage());
        }

        try {
            Machine m1 =new Machine(1, "rapide","F01", 20.0);
            m1.setDureeTypeOperation(5);
            m1.save();
        } catch(SQLException exc) {
            System.out.println("ERREUR m1.save " + exc.getLocalizedMessage());
        }
   }  
    
    public static void razBDD() {
        try {
            deleteSchema();
        } catch(SQLException exc) {
            System.out.println("ERREUR deleteSchema " + exc.getLocalizedMessage());
        }
        try {
            creeSchema();
        } catch(SQLException exc) {
            System.out.println("ERREUR creeSchema " + exc.getLocalizedMessage());
        }
        initTest();
    }

    public static void menuPrincipal() {
        int rep = -1;
        while (rep != 0) {
            int i = 1;
            System.out.println("Menu principal");
            System.out.println("==============");
            System.out.println((i++) + ") supprimer schéma");
            System.out.println((i++) + ") créer schéma");
            System.out.println((i++) + ") RAZ BDD = supp + crée + init");
            System.out.println((i++) + ") gestion des utilisateurs");
            System.out.println("0) Fin");
            rep = ConsoleFdB.entreeEntier("Votre choix : ");
            try {
                int j = 1;
                if (rep == j++) {
                    deleteSchema();
                } else if (rep == j++) {
                    creeSchema();
                } else if (rep == j++) {
                    razBDD();
                } else if (rep == j++) {
                    menuUtilisateur();
                }
            } catch (SQLException ex) {
                System.out.println(ExceptionsUtils.messageEtPremiersAppelsDansPackage(ex, "fr.insa.beuvron", 5));
            } finally {}
        }
    }

    public static void menuUtilisateur() {
        Connection conn = getConnection();
        int rep = -1;
        while (rep != 0) {
            int i = 1;
            System.out.println("Menu utilisateur");
            System.out.println("================");
            System.out.println((i++) + ") lister les utilisateurs");
            System.out.println((i++) + ") ajouter un utilisateur");
            System.out.println("0) Fin");
            rep = ConsoleFdB.entreeEntier("Votre choix : ");
            try {
                int j = 1;
                if (rep == j++) {
                    List<Utilisateur> users = Utilisateur.tousLesUtilisateurs(conn);
                    System.out.println(users.size() + " utilisateurs : ");
                    System.out.println(ListUtils.enumerateList(users));
                } else if (rep == j++) {
                    System.out.println("entrez un nouvel utilisateur : ");
                    Utilisateur nouveau = Utilisateur.demande();
                    nouveau.saveInDBV1(conn);
                }
            } catch (SQLException ex) {
                System.out.println(ExceptionsUtils.messageEtPremiersAppelsDansPackage(ex, "fr.insa.beuvron", 5));
            }
        }
    }
}
