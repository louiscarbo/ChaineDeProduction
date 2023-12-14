/*
Copyright 2000- Francois de Bertrand de Beuvron

This file is part of CoursBeuvron.

CoursBeuvron is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

CoursBeuvron is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with CoursBeuvron.  If not, see <http://www.gnu.org/licenses/>.
 */
package fr.insa.developpement.model;

import fr.insa.beuvron.utils.ConsoleFdB;
import fr.insa.beuvron.utils.exceptions.ExceptionsUtils;
import fr.insa.beuvron.utils.list.ListUtils;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 *
 * @author francois
 */
public class GestionBDD {

    private Connection conn;

    public GestionBDD(Connection conn) {
        this.conn = conn;
    }

    public static Connection connectGeneralMySQL(String host,
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

    public static String getPassPourServeurM3() {
        // en phase de développement, je vous conseille de mettre votre 
        // mot de passe en clair pour ne pas avoir à le retaper à chaque exécution
        // je ne veux pas mettre le mien dans ce programme que tout le monde
        // peut télécharger
//        return "monpass";
        // vous pouvez aussi le demander à chaque fois
//        return ConsoleFdB.entreeString("pass pour serveur M3 : ");
        // ici je le lit dans un fichier que j'ai exclu de git (.gitignore)
        try (BufferedReader bin = new BufferedReader(new FileReader("pass.txt"))) {
            return bin.readLine();
        } catch (IOException ex) {
            throw new Error("impossible de lire le mot de passe", ex);
        }
    }

    public static Connection connectSurServeurM3() throws SQLException {
        return connectGeneralMySQL("92.222.25.165", 3306,
                "m3_hgounon01", "m3_hgounon01",
                "ae2fe50b");
    }

    /**
     * Creation du schéma. On veut créer tout ou rien, d'où la gestion explicite
     * des transactions.
     *
     * @throws SQLException
     */
    public void creeSchema() throws SQLException {
        this.conn.setAutoCommit(false);
        try (Statement st = this.conn.createStatement()) {
            st.executeUpdate(
                    "create table li_utilisateur (\n"
                    + "    id integer not null primary key AUTO_INCREMENT,\n"
                    + "    nom varchar(30) not null unique,\n"
                    + "    pass varchar(30) not null\n"
                    + ")\n"
            );
            st.executeUpdate(
                    "create table li_likes (\n"
                    + "    u1 integer not null,\n"
                    + "    u2 integer not null\n"
                    + ")\n"
            );
            this.conn.commit();
            st.executeUpdate(
                    "alter table li_likes \n"
                    + "    add constraint fk_li_likes_u1 \n"
                    + "    foreign key (u1) references li_utilisateur(id) \n"
            );
            st.executeUpdate(
                    "alter table li_likes \n"
                    + "    add constraint fk_li_likes_u2 \n"
                    + "    foreign key (u2) references li_utilisateur(id) \n"
            );
        } catch (SQLException ex) {
            this.conn.rollback();
            throw ex;
        } finally {
            this.conn.setAutoCommit(true);
        }
    }

    /**
     * Suppression du schéma. Le schéma n'est peut-être pas créé, ou pas
     * entièrement créé, on ne s'arrête donc pas en cas d'erreur : on ne fait
     * que passer à la suite
     *
     * @throws SQLException
     */
    public void deleteSchema() throws SQLException {
        try (Statement st = this.conn.createStatement()) {
            // pour être sûr de pouvoir supprimer, il faut d'abord supprimer les liens
            // puis les tables
            // suppression des liens
            try {
                st.executeUpdate("alter table li_likes drop constraint fk_li_likes_u1");
            } catch (SQLException ex) {
                // nothing to do : maybe the constraint was not created
            }
            try {
                st.executeUpdate("alter table li_likes drop constraint fk_li_likes_u2");
            } catch (SQLException ex) {
            }
            // je peux maintenant supprimer les tables
            try {
                st.executeUpdate("drop table li_likes");
            } catch (SQLException ex) {
            }
            try {
                st.executeUpdate("drop table li_utilisateur");
            } catch (SQLException ex) {
            }
        }
    }

    public void initTest() throws SQLException {
        Utilisateur fdb = new Utilisateur("fdb", "pass");
        fdb.saveInDBV1(this.conn);
        Utilisateur toto = new Utilisateur("toto", "pass");
        toto.saveInDBV1(this.conn);
    }

    public void razBDD() throws SQLException {
        this.deleteSchema();
        this.creeSchema();
        this.initTest();
    }

    public void menuUtilisateur() {
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
                    List<Utilisateur> users = Utilisateur.tousLesUtilisateurs(this.conn);
                    System.out.println(users.size() + " utilisateurs : ");
                    System.out.println(ListUtils.enumerateList(users));
                } else if (rep == j++) {
                    System.out.println("entrez un nouvel utilisateur : ");
                    Utilisateur nouveau = Utilisateur.demande();
                    nouveau.saveInDBV1(this.conn);
                }
            } catch (SQLException ex) {
                System.out.println(ExceptionsUtils.messageEtPremiersAppelsDansPackage(ex, "fr.insa.beuvron", 5));
            }
        }
    }

    public void menuPrincipal() {
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
                    this.deleteSchema();
                } else if (rep == j++) {
                    this.creeSchema();
                } else if (rep == j++) {
                    this.razBDD();
                } else if (rep == j++) {
                    this.menuUtilisateur();
                }
            } catch (SQLException ex) {
                System.out.println(ExceptionsUtils.messageEtPremiersAppelsDansPackage(ex, "fr.insa.beuvron", 5));
            }
        }
    }

    
    public static void debut() {
        try (Connection con = connectSurServeurM3()) {
            System.out.println("connecté");
            GestionBDD gestionnaire = new GestionBDD(con);
            gestionnaire.menuPrincipal();
        } catch (SQLException ex) {
            throw new Error("Connection impossible", ex);
        }
    }

    public static void main(String[] args) {
        debut();
    }

    /**
     * @return the conn
     */
    public Connection getConn() {
        return conn;
    }
}
