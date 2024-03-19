import { Component, inject } from '@angular/core';
import { AsyncPipe, JsonPipe } from '@angular/common';
import { ReactiveFormsModule, FormControl } from '@angular/forms';
import { BehaviorSubject, filter, map, switchMap, catchError, of, scan } from 'rxjs';

import { DataService, Data } from './data.service';
import { AuthService } from './auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  templateUrl: './app.component.html',
  imports: [
    AsyncPipe,
    ReactiveFormsModule,
    JsonPipe,
  ],
})
export class AppComponent {
  private dataClient = inject(DataService);
  private authService = inject(AuthService);
  users = [null, 'admin', 'manager', 'user'] as const;


  userControl: FormControl<typeof this.users[number]> = new FormControl();
  dataControl: FormControl<string> = new FormControl<string>('', {nonNullable: true});

  active$ = new BehaviorSubject<string | null>(null);
  results$ =  this.active$.pipe(
    filter((value): value is string => value !== null && value.trim() !== ''),
    switchMap(id => this.dataClient.start(id).pipe(
      switchMap(() => this.dataClient.listen(id)),
      scan((acc, value) => [...acc, value], <Data[]>[]),
      catchError(() => of(<Data[]>[])),
    )),
    map(arr => [...arr].reverse())
  );


  retrieveData() {
    const value = this.dataControl.value;
    if(value.trim() != '') {
      this.active$.next(value);
    }
  }

  stopEarly() {
    const value = this.dataControl.value;
    if(value.trim() != '') {
      this.dataClient.stop(value).subscribe();
    }
  }

  setUser() {
    const value = this.userControl.value;
    if(value != null)
      this.authService.login(value);
    else
      this.authService.logout();
  }
}
