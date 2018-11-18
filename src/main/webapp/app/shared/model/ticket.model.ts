import { Moment } from 'moment';
import { ILabel } from 'app/shared/model//label.model';

export interface ITicket {
    id?: number;
    title?: string;
    description?: string;
    dueDate?: Moment;
    done?: boolean;
    projectName?: string;
    projectId?: number;
    assignedToLogin?: string;
    assignedToId?: number;
    labels?: ILabel[];
}

export class Ticket implements ITicket {
    constructor(
        public id?: number,
        public title?: string,
        public description?: string,
        public dueDate?: Moment,
        public done?: boolean,
        public projectName?: string,
        public projectId?: number,
        public assignedToLogin?: string,
        public assignedToId?: number,
        public labels?: ILabel[]
    ) {
        this.done = this.done || false;
    }
}
