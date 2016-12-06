package dao;

import Core.Expections.*;

import java.util.regex.PatternSyntaxException;

public class Godzina {
	
	private int godzina;
	private int minuta;
	private String nazwa;
	
	public Godzina(String temp)throws GodzinaFormatException{
		try{
			nazwa = temp;
			String[] tempTablica = temp.split(":");
			if(tempTablica.length != 2)
				throw new GodzinaFormatException(temp);
			if(tempTablica[0] == null || tempTablica[1] == null)
				throw new GodzinaFormatException(temp);
			godzina = Integer.parseInt(tempTablica[0]);
			minuta = Integer.parseInt(tempTablica[1]);
			if(godzina<0 || godzina>23 || minuta<0 || minuta>59)
				throw new GodzinaFormatException(temp);
		}catch(PatternSyntaxException w){
			throw new GodzinaFormatException(temp);
		}catch(NumberFormatException w){
			throw new GodzinaFormatException(temp);
		}
	}

	public Godzina(Godzina poczatekCzasu, String dlugosc) throws GodzinaFormatException{
		try{
			String[] tempTablica = dlugosc.split(":");
			int godzinaD = 0, minutaD = 0;
			if(tempTablica.length != 2)
				throw new GodzinaFormatException(dlugosc);
			if(tempTablica[0] == null || tempTablica[1] == null)
				throw new GodzinaFormatException(dlugosc);
			godzinaD = Integer.parseInt(tempTablica[0]);
			minutaD = Integer.parseInt(tempTablica[1]);
			if(godzinaD<0 || minutaD<0 || minutaD>59)
				throw new GodzinaFormatException(dlugosc);
			
			int tempGodzina = poczatekCzasu.getGodzina()+godzinaD;
			int tempMinuta = poczatekCzasu.getMinuta()+minutaD;
			if(tempMinuta >= 60){
				tempGodzina++;
				tempMinuta=tempMinuta%60;
			}
	
			this.godzina = tempGodzina;
			this.minuta = tempMinuta;
		
			String tempNazwa = ""+godzina+":";
			if(this.minuta<10)tempNazwa+="0";
			tempNazwa+=this.minuta;
			this.nazwa = tempNazwa;
		}catch(PatternSyntaxException w){
			throw new GodzinaFormatException(dlugosc);
		}catch(NumberFormatException w){
			throw new GodzinaFormatException(dlugosc);
		}
	}

	public boolean czyWiekszy(Godzina czas) {
		if(this.godzina > czas.getGodzina())return true;
		else if (this.godzina < czas.getGodzina())return false;
		else return this.minuta > czas.getMinuta();
	}
	
	public boolean czyNieMniejszy(Godzina czas) {
		if(this.godzina > czas.getGodzina())return true;
		else if (this.godzina < czas.getGodzina())return false;
		else return this.minuta >= czas.getMinuta();
	}

	public int getMinuta() {
		return this.minuta;
	}

	public int getGodzina() {
		return this.godzina;
	}

	public boolean czyNieMniejszy1(Godzina czas) {
		return this.getHash() >= (czas.getHash()+1);
	}
	
	public boolean czyNieMniejszy5(Godzina czas) {
		return this.getHash() >= (czas.getHash()+5);
	}

	public String getString() {
		return nazwa;
	}
	
	public int getHash(){
		return 60*godzina+minuta;
	}
}
