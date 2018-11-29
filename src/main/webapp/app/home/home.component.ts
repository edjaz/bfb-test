import { Component, OnInit } from '@angular/core';
import { NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { Account, LoginModalService, Principal } from 'app/core';
import { TicketService } from 'app/entities/ticket';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { ITicket } from 'app/shared/model/ticket.model';

@Component({
    selector: 'jhi-home',
    templateUrl: './home.component.html',
    styleUrls: ['home.scss']
})
export class HomeComponent implements OnInit {
    account: Account;
    modalRef: NgbModalRef;

    myTickets: ITicket[];

    constructor(
        private principal: Principal,
        private ticketService: TicketService,
        private loginModalService: LoginModalService,
        private eventManager: JhiEventManager
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
}
