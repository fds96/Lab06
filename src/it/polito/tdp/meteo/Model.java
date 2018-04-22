package it.polito.tdp.meteo;

import java.util.*;

import it.polito.tdp.meteo.bean.Citta;
import it.polito.tdp.meteo.bean.Rilevamento;
import it.polito.tdp.meteo.bean.SimpleCity;
import it.polito.tdp.meteo.db.MeteoDAO;

public class Model {

	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	private MeteoDAO meteo;
	private double costoMigliorPercorso;
	List<Citta> citta;
	List<SimpleCity> soluzione;

	public Model() {
		 meteo = new MeteoDAO();
		 citta = new ArrayList<>();
	}

	public String getUmiditaMedia(int mese) {
		String result="";
		for(String s : meteo.getAllLocalita()) {
			double temp = meteo.getAvgRilevamentiLocalitaMese(mese, s);
			result += (s + " " + temp + "\n");
		}
		return ("Nel mese " + mese + " l'umidità media è stata: \n" +result);
	}

	public String trovaSequenza(int mese) {
		citta.clear();
		for(String s : meteo.getAllLocalita()) {
			citta.add(new Citta(s,meteo.getAllRilevamentiLocalitaMese(mese, s)));
		}
		costoMigliorPercorso=Double.MAX_VALUE;
		this.recursive(new ArrayList<SimpleCity>(), citta,0);
		return soluzione.toString();
	}

	private void recursive(ArrayList<SimpleCity> parziale, List<Citta> cities, int livello) {
		if(!this.controllaParziale(parziale)) {
			return;
		}
		if(!this.controllaGiorniConsecutivi(parziale)) {
			return;
		}
		if(livello>=NUMERO_GIORNI_TOTALI) {
			double punteggio = this.punteggioSoluzione(parziale);
			if(punteggio<costoMigliorPercorso) {
				costoMigliorPercorso=punteggio;
				soluzione = new ArrayList<>(parziale);
				return;
			}
		}
		for(Citta c : cities) {
			parziale.add(this.getSimpleCity(c));
			this.recursive(parziale, cities, livello+1);
			parziale.remove(livello);
		}
	}

	private boolean controllaGiorniConsecutivi(ArrayList<SimpleCity> parziale) {
		//Ritorna falso se ci sono meno dei giorni minimi consentiti
		int contatore=1;
		for(int i=1; i<parziale.size();i++) {
			if(parziale.get(i).equals(parziale.get(i-1)))
				contatore++;
			else {
				if (contatore <NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN)
					return false;
				else{
					contatore=1;
				}
			}
		}
		return true;
	}

	public SimpleCity getSimpleCity(Citta c) {
		return new SimpleCity(c.getNome(),0);
	}

	private Double punteggioSoluzione(List<SimpleCity> soluzioneCandidata) {
		double punteggio = 0.0;
		for(int i=0;i<soluzioneCandidata.size();i++) {
			for(Citta c : citta) {
				if(c.getNome().equals(soluzioneCandidata.get(i).getNome())) {
					soluzioneCandidata.get(i).setCosto(c.getRilevamenti().get(i).getUmidita());
					punteggio+=c.getRilevamenti().get(i).getUmidita();
					if(i>0 && !soluzioneCandidata.get(i).equals(soluzioneCandidata.get(i-1))) {
						soluzioneCandidata.get(i).addCosto(COST);
						punteggio+=COST;
					}
				}
			}
		}
		return punteggio;
	}

	private boolean controllaParziale(List<SimpleCity> parziale) {
		//Ritorna false se maggiore dei giorni massimi consentiti
		int contatore;
		for(SimpleCity s1 : parziale) {
			contatore=0;
			for(SimpleCity s2 : parziale) {
				if(s1.equals(s2))
					contatore ++;
				if(contatore>NUMERO_GIORNI_CITTA_MAX)
					return false;
			}
		}
		return true;
	}
	
	public double getCostoMigliorPercorso () {
		return costoMigliorPercorso;
	}

}
