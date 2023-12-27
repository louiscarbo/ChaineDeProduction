package fr.insa.developpement.model;

import fr.insa.developpement.model.classes.Machine;
import fr.insa.developpement.model.classes.TypeOperation;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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

    public void creeSchema() throws SQLException {
        this.conn.setAutoCommit(false);
        try (Statement st = this.conn.createStatement()) {
            st.executeUpdate(
                    "create table machine (\n"
                    + "    id integer not null primary key AUTO_INCREMENT,\n"
                    + "    ref varchar(30) not null unique,\n"
                    + "    des varchar(100) not null,\n"
                    + "    puissance double not null\n"   
                    + ")\n"
            );
             st.executeUpdate(
                    "create table realise (\n"
                    + "    idMachine integer null,\n"
                    + "    idType integer null unique,\n"
                    + "    duree integer not null\n"        
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
                    "create table produit (\n"
                    + "    id integer not null primary key AUTO_INCREMENT,\n"
                    + "    ref varchar(30) not null unique,\n"
                    + "    des varchar(100) not null\n"  
                    + ")\n"
            );
            st.executeUpdate(
                    "create table operation (\n"
                    + "    id integer not null primary key AUTO_INCREMENT,\n"
                    + "    idType integer not null unique,\n"
                    + "    idproduit integer not null unique\n"  
                    + ")\n"
            );
            st.executeUpdate(
                "CREATE INDEX fk_machine_id ON realise (idMachine)"
            );
            // st.executeUpdate(
            //         "alter table machine \n"
            //         + "    add constraint fk_machine_id \n"
            //         + "    foreign key (id) references realise(idMachine) \n"
            // );
            // st.executeUpdate(
            //         "alter table typeoperation \n"
            //         + "    add constraint fk_typeoperation_id \n"
            //         + "    foreign key (id) references realise(idType) \n"
            // );
            this.conn.commit();
        } catch (SQLException ex) {
            this.conn.rollback();
            throw ex;
        } finally {
            this.conn.setAutoCommit(true);
        }
    }

    public void deleteSchema() throws SQLException {
        try (Statement st = this.conn.createStatement()) {
            // pour être sûr de pouvoir supprimer, il faut d'abord supprimer les liens
            // puis les tables
            // suppression des liens
            try {
                st.executeUpdate("alter table machine drop constraint fk_machine_id");
            } catch (SQLException ex) {
                // nothing to do : maybe the constraint was not created
            }
            try {
                st.executeUpdate("alter table typeoperation drop constraint fk_typeoperation_id");
            } catch (SQLException ex) {
            }
            // je peux maintenant supprimer les tables
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
        }
    }

    public void initTest(){
        try {
            TypeOperation t1= new TypeOperation(1, "Fraisage", "");
            t1.save(this.conn);
        } catch (SQLException exc) {
            System.out.println("ERREUR t1.save " + exc.getLocalizedMessage());
        }

        try {
            Machine m1 =new Machine(1, "rapide","F01", 20.0);
            m1.save(this.conn);
        } catch(SQLException exc) {
            System.out.println("ERREUR m1.save " + exc.getLocalizedMessage());
        }
   }  
    
    public void razBDD() {
        try {
            this.deleteSchema();
        } catch(SQLException exc) {
            System.out.println("ERREUR deleteSchema " + exc.getLocalizedMessage());
        }
        try {
            this.creeSchema();
        } catch(SQLException exc) {
            System.out.println("ERREUR creeSchema " + exc.getLocalizedMessage());
        }
        try {
            Machine.fillMachineTable(this.conn);
        } catch(SQLException exc) {
            System.out.println("ERREUR fillMachineTable " + exc.getLocalizedMessage());
        }
        this.initTest();
    }

    public Connection getConn() {
        return conn;
    }
}
