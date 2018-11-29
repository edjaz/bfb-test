import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { TfTestSharedModule } from 'app/shared';
import { HOME_ROUTE, HomeComponent } from './';
import { MineTicketComponent } from 'app/home/mine-ticket.component';

@NgModule({
    imports: [TfTestSharedModule, RouterModule.forChild([HOME_ROUTE])],
    declarations: [HomeComponent, MineTicketComponent],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class TfTestHomeModule {}
