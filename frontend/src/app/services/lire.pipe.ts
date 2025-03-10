import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  standalone: true,
  name: 'lire'
})
export class LirePipe implements PipeTransform {

  // Se i valori che hai già sono in Euro e vuoi convertirli in Lire:
  private readonly LIRE_PER_EURO = 1936.27; // Tasso di conversione storico

  transform(value: number, alreadyInLire: boolean = false): string {
    let lireValue: number;

    if (alreadyInLire) {
      // Se il valore è già in Lire, non facciamo alcuna conversione
      lireValue = value;
    } else {
      // Altrimenti convertiamo da Euro a Lire
      lireValue = value * this.LIRE_PER_EURO;
    }

    // Formattazione con separatore migliaia (opzionale)
    // 'it-IT' per formattare con il punto come separatore delle migliaia
    const formatted = new Intl.NumberFormat('it-IT', {
      maximumFractionDigits: 0 // niente decimali
    }).format(lireValue);

    // Aggiungiamo la L di Lire
    return `${formatted} L`;
  }

}
