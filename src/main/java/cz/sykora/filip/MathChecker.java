package cz.sykora.filip;

public class MathChecker {
    //Pokud zjistí číslo, kterým je dělitelné, tak vyhodí false, jinak true;
    public static boolean isPrime(long num){
        if(num <= 1) return false;
        if(num == 2) return true;
        //Checkuje dělitelnost dvěmi, ať můžeme checkovat jen lichá čísla (eliminuje polovinu čísel pro check)
        if(num % 2 == 0) return false;
        //Prochází všechna lichá čísla až do odmocniny num a checkuje dělitelnost
        for(long i = 3; i*i <= num; i += 2){
            if(num % i == 0) return false;
        }
        return true;
    }
}
