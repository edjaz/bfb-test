import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
import { JhiAlertService } from 'ng-jhipster';

import { ITicket } from 'app/shared/model/ticket.model';
import { TicketService } from './ticket.service';
import { IProject } from 'app/shared/model/project.model';
import { ProjectService } from 'app/entities/project';
import { IUser, UserService } from 'app/core';
import { ILabel } from 'app/shared/model/label.model';
import { LabelService } from 'app/entities/label';

@Component({
    selector: 'jhi-ticket-update',
    templateUrl: './ticket-update.component.html'
})
export class TicketUpdateComponent implements OnInit {
    ticket: ITicket;
    isSaving: boolean;

    projects: IProject[];

    users: IUser[];

    labels: ILabel[];
    dueDateDp: any;

    constructor(
        private jhiAlertService: JhiAlertService,
        private ticketService: TicketService,
        private projectService: ProjectService,
        private userService: UserService,
        private labelService: LabelService,
        private activatedRoute: ActivatedRoute
    ) {}

    ngOnInit() {
        this.isSaving = false;
        this.activatedRoute.data.subscribe(({ ticket }) => {
            this.ticket = ticket;
        });
        this.projectService.query().subscribe(
            (res: HttpResponse<IProject[]>) => {
                this.projects = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
        this.userService.query().subscribe(
            (res: HttpResponse<IUser[]>) => {
                this.users = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
        this.labelService.query().subscribe(
            (res: HttpResponse<ILabel[]>) => {
                this.labels = res.body;
            },
            (res: HttpErrorResponse) => this.onError(res.message)
        );
    }

    previousState() {
        window.history.back();
    }

    save() {
        this.isSaving = true;
        if (this.ticket.id !== undefined) {
            this.subscribeToSaveResponse(this.ticketService.update(this.ticket));
        } else {
            this.subscribeToSaveResponse(this.ticketService.create(this.ticket));
        }
    }

    private subscribeToSaveResponse(result: Observable<HttpResponse<ITicket>>) {
        result.subscribe((res: HttpResponse<ITicket>) => this.onSaveSuccess(), (res: HttpErrorResponse) => this.onSaveError());
    }

    private onSaveSuccess() {
        this.isSaving = false;
        this.previousState();
    }

    private onSaveError() {
        this.isSaving = false;
    }

    private onError(errorMessage: string) {
        this.jhiAlertService.error(errorMessage, null, null);
    }

    trackProjectById(index: number, item: IProject) {
        return item.id;
    }

    trackUserById(index: number, item: IUser) {
        return item.id;
    }

    trackLabelById(index: number, item: ILabel) {
        return item.id;
    }

    getSelected(selectedVals: Array<any>, option: any) {
        if (selectedVals) {
            for (let i = 0; i < selectedVals.length; i++) {
                if (option.id === selectedVals[i].id) {
                    return selectedVals[i];
                }
            }
        }
        return option;
    }
}
