import { Component, OnInit } from '@angular/core';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { Account, LoginModalService, Principal } from 'app/core';
import { TicketService } from 'app/entities/ticket';
import { HttpClient, HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { ITicket } from 'app/shared/model/ticket.model';
import { SERVER_API_URL } from 'app/app.constants';

@Component({
    selector: 'jhi-home',
    templateUrl: './home.component.html',
    styleUrls: ['home.scss']
})
export class HomeComponent implements OnInit {
    account: Account;
    modalRef: NgbModalRef;

    myTickets: ITicket[];
    original: number[];
    max: number;
    sum: number;
    up6: number[];

    constructor(
        private principal: Principal,
        private ticketService: TicketService,
        private loginModalService: LoginModalService,
        private eventManager: JhiEventManager,
        private httpClient: HttpClient
    ) {}

    ngOnInit() {
        this.principal.identity().then(account => {
            this.account = account;
            if (this.account) {
                this.loadMyTickets();
            }
        });
        this.registerAuthenticationSuccess();
    }

    registerAuthenticationSuccess() {
        this.eventManager.subscribe('authenticationSuccess', message => {
            this.principal.identity().then(account => {
                this.account = account;
                if (this.account) {
                    this.loadMyTickets();
                }
            });
        });
    }

    loadMyTickets() {
        this.ticketService
            .findMine()
            .subscribe((res: HttpResponse<ITicket[]>) => (this.myTickets = res.body), (res: HttpErrorResponse) => alert(res.message));
    }

    isAuthenticated() {
        return this.principal.isAuthenticated();
    }

    login() {
        this.modalRef = this.loginModalService.open();
    }

    computeRandomServer() {
        this.httpClient.get<number[]>(SERVER_API_URL + 'api/random').subscribe(arrays => this.computeValues(arrays));
    }

    private computeValues(arrays: number[]) {
        this.original = arrays;
        this.max = arrays.reduce((acc, val) => (acc < val ? val : acc));
        this.sum = arrays.reduce((acc, val) => acc + val);
        this.up6 = arrays.filter(v => v >= 6).sort((a, b) => a - b);
    }
}
