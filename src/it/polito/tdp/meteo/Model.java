package it.polito.tdp.meteo;

import java.util.*;

import it.polito.tdp.meteo.bean.Citta;
import it.polito.tdp.meteo.bean.SimpleCity;
import it.polito.tdp.meteo.db.MeteoDAO;

public class Model {

	private final static int COST = 100;
	private final static int NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN = 3;
	private final static int NUMERO_GIORNI_CITTA_MAX = 6;
	private final static int NUMERO_GIORNI_TOTALI = 15;
	private MeteoDAO meteo;
	private double costoMigliorPercorso=0.0;
	List<Citta> citta;
	List<SimpleCity> soluzione;

	public Model() {
		 meteo = new MeteoDAO();
	}

	public String getUmiditaMedia(int mese) {
		String result="";
		for(String s : meteo.getAllLocalita()) {
			double temp = meteo.getAvgRilevamentiLocalitaMese(mese, s).;
			result += (s + " " + temp + "\n");
		}
		return ("Nel mese " + mese + " l'umidità media è stata: \n" +result);
	}

	public String trovaSequenza(int mese) {
		citta = new ArrayList<>();
		for(String s : meteo.getAllLocalita()) {
			citta.add(new Citta(s,meteo.getAllRilevamentiLocalitaMese(mese, s)));
		}
		this.costoMigliorPercorso=10000000.0;
		this.recursive(new ArrayList<SimpleCity>(), 0, citta);
		return soluzione.toString();
	}
	
	private void recursive(List<SimpleCity> parziale, int livello, List<Citta> dati) {
		if(this.controllaParziale(parziale) == false) {
			return;
		}
		if(livello>=NUMERO_GIORNI_TOTALI) {
			if(this.punteggioSoluzione(parziale)>=this.costoMigliorPercorso) {
				return;
			}
			costoMigliorPercorso = this.punteggioSoluzione(parziale);
			soluzione = new ArrayList<>(parziale);
			return;
		}
		for(Citta c : dati) {
			if(diversaSimpleCity(c,parziale)) {
				for(int i=0; i< NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN;i++) {
					SimpleCity temp = this.getSimpleCity(c,parziale,livello);
					parziale.add(temp);
					c.increaseCounter();
				}
				this.recursive(parziale, livello+3, dati);
				for(int i=0; i< NUMERO_GIORNI_CITTA_CONSECUTIVI_MIN;i++) {
					parziale.remove(parziale.size()-1);
					c.decreaseCounter();
				}
			}
			else {
				SimpleCity temp = this.getSimpleCity(c,parziale,livello);
				parziale.add(temp);
				c.increaseCounter();
				this.recursive(parziale, livello+1, dati);
				parziale.remove(temp);
				c.decreaseCounter();
			}
		}
		
	}

	private boolean diversaSimpleCity(Citta citta, List<SimpleCity> parziale) {
		if(parziale.size()==0)
			return true;
		return !citta.getNome().equals(parziale.get(parziale.size()-1).getNome());
	}

	private SimpleCity getSimpleCity(Citta citta, List<SimpleCity> parziale, int livello) {
		int costo = 0;
		if(this.diversaSimpleCity(citta, parziale)) {
			costo+=COST;
		}
		costo+=citta.getRilevamenti().get(livello).getUmidita();
		return new SimpleCity(citta.getNome(),costo);
	}

	private Double punteggioSoluzione(List<SimpleCity> soluzioneCandidata) {
		double score = 0.0;
		for(SimpleCity s : soluzioneCandidata) {
			score+=s.getCosto();
		}
		return score;
	}

	private boolean controllaParziale(List<SimpleCity> parziale) {
		for(SimpleCity s: parziale) {
			for(Citta c : this.citta) {
				if(s.getNome().equals(c.getNome())) {
					if(c.getCounter()>NUMERO_GIORNI_CITTA_MAX)
						return false;
				}
			}
		}
		return true;
	}

}
