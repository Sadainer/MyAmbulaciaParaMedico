package com.example.admin_sena.myambulaciaparamedico;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;


public class ListaClinicas {
    ArrayList<Clinica> listaClinicas;

    public ListaClinicas() {
        this.listaClinicas = new ArrayList<>();
        llenarLista();
    }

    public void llenarLista(){
        listaClinicas.add(new Clinica("Clinica Laura Daniela"               ,"Cr19 NO 14-47"    , new LatLng(10.4706104,-73.2593356)));
        listaClinicas.add(new Clinica("Clinica Cesar"                       ,"Cl 16 NO 14-90 "  , new LatLng(10.472058,-73.2543377)));
        listaClinicas.add(new Clinica("Clinica Valledupar"                  ,"Cl 16 NO 15-15"   , new LatLng(10.471491,-73.2544852)));
     //   listaClinicas.add(new Clinica("Hospital Eduardo Arredondo CDV","", new LatLng()));
        listaClinicas.add(new Clinica("Hospital Eduardo Arredondo S. Martin","CRA 20 No 43 - 63", new LatLng(10.446821, -73.249169)));
        listaClinicas.add(new Clinica("Hospital Eduardo Arredondo 450 años" ,""                 , new LatLng(10.476731, -73.283246)));
        listaClinicas.add(new Clinica("Hospital Eduardo Arredondo Nevada"   ,"CLL 6 N° 42-55"   , new LatLng(10.476754,-73.283247)));
        listaClinicas.add(new Clinica("Hospital Rosario Pumarejo"           ,""                 , new LatLng(10.471491,-73.2544852)));
        listaClinicas.add(new Clinica("Clinica Erasmo"                      ,"Cr 19 4 C-72"     , new LatLng(10.48782,-73.2663187)));
      //  listaClinicas.add(new Clinica("Clinica de Fracturas","", new LatLng()));
        listaClinicas.add(new Clinica("Clinica Santa Isabel"                ,"Cra. 18d #22-33"  , new LatLng(10.461928, -73.250482)));
        listaClinicas.add(new Clinica("Clinica Medicos Ltda."               ,""                 , new LatLng(10.4724154,-73.2496831)));
        listaClinicas.add(new Clinica("Clinica Santo Tomas"                 ,"Cra. 10 #131"     , new LatLng(10.476872, -73.249381)));
        listaClinicas.add(new Clinica("Clinica Arenas"                      ,"Cl. 16 #1827"     , new LatLng(10.469031, -73.255561)));

    }
}
