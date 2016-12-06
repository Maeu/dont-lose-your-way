package Core.wyszukiwarka;

import Core.Expections.*;
import Core.comparators.CzasComparator;
import Core.comparators.WygodaComparator;
import dao.*;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.PatternSyntaxException;

public class Wyszukiwarka implements WyszukiwarkaPolaczen {

	private List<Kurs> kursy;
	private List<Trasa> trasy;
	private Map<String, Linia> linie;
	private Map<String, Przystanek> przystanki;
	private Set<Polaczenie> polaczenia;
	private List<Stack<ElementStosu>> listaStosow;

	public Wyszukiwarka(){
		kursy = new LinkedList<Kurs>();
		trasy = new LinkedList<Trasa>();
		linie = new HashMap<String, Linia>();
		przystanki = new HashMap<String, Przystanek>();
	}

	public static void main(String args[]){
		try{
			String wynik;
			Scanner wejscie = new Scanner(System.in);

			if(!wejscie.hasNextLine())throw new BrakDanychException();
			String liniaWejscia = wejscie.nextLine();
			wejscie.close();

			String daneWejscia[] = liniaWejscia.split(" ");
			int index = 0;

			Wyszukiwarka w = new Wyszukiwarka();
			w.wczytajBaze();

			String przystanekPoczatkowy = null;
			String przystanekDocelowy = null;
			for(int i=0; i<=1; i++){
				String tempPrzystanek;
				if(index >= daneWejscia.length)throw new BrakDanychException();
				tempPrzystanek = daneWejscia[index];
				index++;
				boolean znalazlem = false;
				while(index < daneWejscia.length && !znalazlem){
					tempPrzystanek += " "+daneWejscia[index];
					index++;
					znalazlem = w.czyIstnieje(tempPrzystanek);
				}
				if(!znalazlem)throw new PrzystanekNotFoundException(tempPrzystanek);
				if(i==0)przystanekPoczatkowy = tempPrzystanek;
				else przystanekDocelowy = tempPrzystanek;
			}

			if(index >= daneWejscia.length)throw new BrakDanychException();
			String czasPoczatkowy = daneWejscia[index];
			index++;
			if(index >= daneWejscia.length)throw new BrakDanychException();
			String dlugosc = daneWejscia[index];
			index++;
			if(index < daneWejscia.length)throw new ZbedneDaneNaKoncuException(index, daneWejscia);

			String porzadek, tryb;
			porzadek = daneWejscia[index];
			index++;
			tryb = daneWejscia[index];
			if(porzadek.equals("s")){
				if(tryb.equals("w"))wynik = w.znajdzOdNajkrotszego(przystanekPoczatkowy, przystanekDocelowy, czasPoczatkowy, dlugosc);
				else wynik = w.znajdzNajkrotsze(przystanekPoczatkowy, przystanekDocelowy, czasPoczatkowy, dlugosc);
			}
			else{
				if(tryb.equals("w"))wynik = w.znajdzOdNajwygodniejszego(przystanekPoczatkowy, przystanekDocelowy, czasPoczatkowy, dlugosc);
				else wynik = w.znajdzNajwygodniejsze(przystanekPoczatkowy, przystanekDocelowy, czasPoczatkowy, dlugosc);
			}
			System.out.print(wynik);
		}catch(ZbedneDaneNaKoncuException w){
			System.out.println("Na końcu wiersza wejścia występują zbędne dane! Usuń fragment: "+w.getZbedne());
		}catch(NiepoprawneDaneWejsciaException w){
			System.out.println("Dane wejscia niezgodne ze specyfikacją! Nierozpoznano fragmentu: "+w.getString());
		}catch(PatternSyntaxException w){
			System.out.println("Dane wejscia niezgodne ze specyfikacją!");
		}catch(GodzinaFormatException w){
			System.out.println("Niepoprawny format godziny! Postać "+w.getGodzina()+" jest niedopuszczalna!");
		}catch(BrakDanychException w){
			System.out.println("Nie wprowadzono wszystkich danych!");
		}catch(FileNotFoundException w){
			System.out.println("Plik pod wskazanym adresem nie istnieje! Wskazana ścieżka to: "+w.getLocalizedMessage());
		}catch(NiepoprawnyRozkladException w){
			System.out.println("Plik zawiera nieprawidłowy format danych! Nieprawidłowy rozkład linii "+w.getLinia()+" na trasie "+w.getNumerTrasy()+". w wierszu "+w.getNumerKursu());
		}catch(BrakDanychWPlikuException w){
			System.out.println("Plik zawiera niekompletne dane!");
		}catch(TaSamaLiniaException w){
			System.out.println("Plik wejściowy zawiera niepoprawne dane! Dane o linii "+w.getLinia()+ " występują w pliku więcej niż raz w pliku wejściowym!");
		}catch(TenSamPrzystanekException w){
			System.out.println("Plik wejściowy zawiera niepoprawne dane! Przystanek o nazwie "+w.getPrzystanek()+" występuje więcej niż raz w bazie przystanków!");
		}catch(PrzystanekNotExistException w){
			System.out.println("Przystanek "+w.getPrzystanek()+", podany w pliku wejściowym jako jeden z przystanków linii "+w.getLinia()+", nie istnieje w bazie!");
		}catch(PrzystanekNotFoundException w){
			System.out.println("Podany na wejściu przystanek ("+w.getPrzystanek()+") nie istnieje w bazie!");
		}
	}

	public void wczytajBaze() {
/*szkic metody
		//pętla po wszystkich przystankach
		String nazwa;
		Przystanek tempPrzystanek = new Przystanek(nazwa);	// FIXME: 06.12.2016
		przystanki.put(nazwa, tempPrzystanek);

		//petla po wszystkich liniach
			String nazwaLinii;
			Linia tempLinia = new Linia(nazwaLinii);    // FIXME: 06.12.2016
			linie.put(nazwaLinii, tempLinia);
			List<Trasa> tempTrasy = new LinkedList<Trasa>();
			String tempLine = null;

				//dla danej linii
					List<Przystanek> tempPrzystanki = new ArrayList<Przystanek>();
							//pętla po przystankach
							String temp;
							tempPrzystanki.add(przystanki.get(temp));

					Trasa tempTrasa = new Trasa(tempPrzystanki);
					tempTrasy.add(tempTrasa);
					trasy.add(tempTrasa);




					for(int j=0; j<2; j++){		//// FIXME: 06.12.2016 dodałem bez sensu tego for'a tylko po to aby zgadzaly sie nawiasy

					//pętla
						Kurs tempKurs = new Kurs(tempLinia, aktTrasa);    // FIXME: 06.12.2016
						kursy.add(tempKurs);
						List<Odjazd> tempOdjazdy = new ArrayList<Odjazd>();

						String temp;
							Godzina tempGodzina = new Godzina(temp);    // FIXME: 06.12.2016
							Odjazd tempOdjazd = new Odjazd(tempKurs, j, tempGodzina);
							tempOdjazdy.add(tempOdjazd);
							Przystanek aktPrzystanek = null;
							if(aktTrasa != null)aktPrzystanek = aktTrasa.getPrzystanek(j);
							if(aktPrzystanek != null)aktPrzystanek.dodajOdjazd(tempOdjazd);
							j++;
						}
						tempKurs.dodajOdjazdy(tempOdjazdy);
*/
	}

	public String znajdzOdNajkrotszego(String start, String stop, String czasStartu, String dlugosc) throws GodzinaFormatException{
		znajdz(start, stop, czasStartu, dlugosc);
		CzasComparator comparator = new CzasComparator();
		polaczenia = new TreeSet<Polaczenie>(comparator);
		zbierzDane();
		return wynik();
	}

	public String znajdzOdNajwygodniejszego(String start, String stop, String czasStartu, String dlugosc) throws GodzinaFormatException{
		znajdz(start, stop, czasStartu, dlugosc);
		WygodaComparator comparator = new WygodaComparator();
		polaczenia = new TreeSet<Polaczenie>(comparator);
		zbierzDane();
		return wynik();
	}

	@Override
	public void wczytajDane(String fileName) throws Exception {

	}

	public String znajdzNajkrotsze(String start, String cel, String czasStart, String dlugosc) throws GodzinaFormatException{
		znajdz(start, cel, czasStart, dlugosc);
		CzasComparator comparator = new CzasComparator();
		polaczenia = new TreeSet<Polaczenie>(comparator);
		zbierzDane();
		return wynikPojedynczy();
	}

	public String znajdzNajwygodniejsze(String start, String cel, String czasStart, String dlugosc) throws GodzinaFormatException{
		znajdz(start, cel, czasStart, dlugosc);
		WygodaComparator comparator = new WygodaComparator();
		polaczenia = new TreeSet<Polaczenie>(comparator);
		zbierzDane();
		return wynikPojedynczy();
	}

	private boolean czyIstnieje(String przystanek){
		return przystanki.containsKey(przystanek);
	}

	private void ustawTrasy(Przystanek przystanek) {
		Iterator<Trasa> it = trasy.iterator();
		while(it.hasNext()){
			it.next().ustawIdDocelowy(przystanek);
		}
	}

	private void znajdz(String start, String cel, String czasStart, String dlugosc) throws GodzinaFormatException{
		this.ustawTrasy(przystanki.get(cel));
		Dfs dfs = new Dfs(przystanki.get(start), przystanki.get(cel), czasStart, dlugosc);
		listaStosow = dfs.getWynik();
	}

	private void zbierzDane(){
		Iterator<Stack<ElementStosu>> itS = listaStosow.iterator();
		while(itS.hasNext()){
			String tempOpis = new String("");
			Stack<ElementStosu> pomStos = itS.next();
			Iterator<ElementStosu> itE = pomStos.iterator();
			ElementStosu pomElt = null;
			ElementStosu remember = null;
			boolean jedziemy = false;
			int ktoryPrzystanek = 0;
			int ileKursow = 0;
			while(itE.hasNext()){
				pomElt = itE.next();
				if(jedziemy){
					ktoryPrzystanek++;
					if(pomElt.getOdjazd().getKurs() != remember.getOdjazd().getKurs()){
						ktoryPrzystanek--;
						tempOpis+=remember.getCzas().getString()+" "+remember.getPrzystanek().getNazwa()+" ("+ktoryPrzystanek+" przystanek)\n";
						jedziemy = false;
						ktoryPrzystanek=0;
					}
				}
				if(!jedziemy){
					tempOpis+="["+pomElt.getLinia().getNazwa()+"] "+pomElt.getCzas().getString()+" "+pomElt.getPrzystanek().getNazwa()+" => ";
					jedziemy = true;
					ileKursow++;
				}
				remember = pomElt;
			}
			Godzina tempCzasDojazdu = remember.getCzas();
			tempOpis+=remember.getCzas().getString()+" "+remember.getPrzystanek().getNazwa()+" ("+ktoryPrzystanek+" przystanek)\n";
			Polaczenie tempPolaczenie = new Polaczenie(tempOpis, tempCzasDojazdu, ileKursow-1);
			polaczenia.add(tempPolaczenie);
		}
	}

	private String wynik(){
		String wynik = "Liczba znalezionych połączeń: "+polaczenia.size()+"\n";
		Iterator<Polaczenie> it = polaczenia.iterator();
		int indeks = 1;
		while(it.hasNext()){
			wynik += "Połączenie "+indeks+":\n" +
			it.next().getOpis() +
			"\n" +
			"";
			indeks++;
		}
		return wynik;
	}

	private String wynikPojedynczy(){
		if(polaczenia.isEmpty()) return "Liczba znalezionych połączeń: 0\n";
		else{
			String wynik = "Liczba znalezionych połączeń: 1\n" +
			"Połączenie 1:\n" +
			polaczenia.iterator().next().getOpis() +
			"\n" +
			"";
			return wynik;
		}
	}
}
