import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  standalone: true,
  name: 'lire'
})
export class LirePipe implements PipeTransform {

  // Se i valori che hai già sono in Euro e vuoi convertirli in Lire:
  private readonly LIRE_PER_EURO = 1936.27; // Tasso di conversione storico

  transform(value: number, alreadyInLire: boolean = false): string {
    if (value == null || isNaN(value)) return '';

    // eur < 10000, lire > 100000
    const euroThreshold = 10000;

    // Heuristic: if > threshold and alreadyInLire not specificato, assume it's already in lire
    const assumeAlreadyInLire = value > euroThreshold && !alreadyInLire;

    const lireValue = assumeAlreadyInLire ? value : value * 1936.27;

    const formatted = new Intl.NumberFormat('it-IT', {
      maximumFractionDigits: 0
    }).format(Math.round(lireValue));

    return `${formatted} L`;
  }


}
