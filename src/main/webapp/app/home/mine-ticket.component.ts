import { Component, Input, OnInit } from '@angular/core';
import { ITicket } from 'app/shared/model/ticket.model';

@Component({
    selector: 'jhi-mine-ticket',
    templateUrl: './mine-ticket.component.html',
    styles: []
})
export class MineTicketComponent implements OnInit {
    @Input()
    myTickets: ITicket[];

    constructor() {}

    ngOnInit() {}
}
