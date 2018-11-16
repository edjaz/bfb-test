import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { TfTestProjectModule } from './project/project.module';
import { TfTestLabelModule } from './label/label.module';
import { TfTestTicketModule } from './ticket/ticket.module';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    // prettier-ignore
    imports: [
        TfTestProjectModule,
        TfTestLabelModule,
        TfTestTicketModule,
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class TfTestEntityModule {}
