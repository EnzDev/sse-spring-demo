import { Injectable, inject, NgZone } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { fetchEventSource, EventStreamContentType } from '@microsoft/fetch-event-source';
import { AuthService } from './auth.service';

class FatalError extends Error {}
class RetryableError extends Error {}

const eventSourceObservable = <T>(zone: NgZone, url: string, headers?: Record<string, string> | undefined) => {
  return new Observable<T>((subscriber) => {
    const abort = new AbortController();
    zone.runOutsideAngular(() => {
      fetchEventSource(url, {
        headers,
        method: 'GET',
        openWhenHidden: true,
        signal: abort.signal,
        onmessage: (event) => {
          const parsed = JSON.parse(event.data)
          zone.run(() => subscriber.next(parsed))
        },
        onerror: (error) => {
          zone.run(() => subscriber.error(error));
          if (error instanceof FatalError) {
            throw error;
          }
        },
        async onopen(response) {
          if (response.ok && response.headers.get('content-type') === EventStreamContentType) {
            return; // everything's good
          } else if (response.status >= 400 && response.status < 500 && response.status !== 429) {
            // client-side errors are usually non-retryable
            throw new FatalError();
          } else {
            throw new RetryableError();
          }
        },
      }).finally(() => zone.run(() => subscriber.complete()));
    });
    return () => abort.abort();
  });
}

export interface Data {
  value: string;
  uuid: string;
  id: string;
  initiator: string;
  nextWait: number;
}

@Injectable({
  providedIn: 'root'
})
export class DataService {
  private http = inject(HttpClient)
  private auth = inject(AuthService)
  private zone = inject(NgZone)

  listen(id: string) {
    return eventSourceObservable<Data>(this.zone, `/api/data/${id}/listen`, {
      Authorization: `Basic ${this.auth.getToken()}`
    })
  }

  start(id: string) {
    return this.http.post(`/api/data/${id}/start`, {});
  }

  stop(id: string) {
    return this.http.post(`/api/data/${id}/stop`, {});
  }
}
