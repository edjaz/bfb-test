import * as express from 'express';
import * as jwt from 'jsonwebtoken';
import * as bodyParser from 'body-parser';
import { Request, Response } from 'express';

const app: express.Application = express();
app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

const port = 8080;

const labels = [
    {
        id: 1051,
        label: 'Incident'
    },
    {
        id: 1052,
        label: 'Feature'
    }
];

const projects = [
    {
        id: 1001,
        name: 'Project1'
    },
    {
        id: 1002,
        name: 'Project2'
    }
];

const tickets = [
    {
        id: 1,
        title: 'fix(crm) : reduit la taille du cookie de session',
        description: 'Réduit la tailler',
        dueDate: '2018-12-06',
        done: false,
        projectId: 1001,
        projectName: 'Project1',
        assignedToId: 4,
        assignedToLogin: 'user',
        labels: [
            {
                id: 1051,
                label: 'Incident'
            }
        ]
    },
    {
        id: 1102,
        title: 'fix(crm) : speed up',
        description: 'travaiiler la performance',
        dueDate: '2018-12-30',
        done: false,
        projectId: 1001,
        projectName: 'Project1',
        assignedToId: 4,
        assignedToLogin: 'user',
        labels: [
            {
                id: 1051,
                label: 'Incident'
            }
        ]
    }
];

const users = [
    {
        id: 1,
        login: 'system',
        firstName: 'System',
        lastName: 'System',
        email: 'system@localhost',
        imageUrl: '',
        activated: true,
        langKey: 'fr',
        createdBy: 'system',
        createdDate: null,
        lastModifiedBy: 'system',
        lastModifiedDate: null,
        authorities: ['ROLE_USER', 'ROLE_ADMIN']
    },
    {
        id: 3,
        login: 'admin',
        firstName: 'Administrator',
        lastName: 'Administrator',
        email: 'admin@localhost',
        imageUrl: '',
        activated: true,
        langKey: 'fr',
        createdBy: 'system',
        createdDate: null,
        lastModifiedBy: 'system',
        lastModifiedDate: null,
        authorities: ['ROLE_USER', 'ROLE_ADMIN']
    },
    {
        id: 4,
        login: 'user',
        firstName: 'User',
        lastName: 'User',
        email: 'user@localhost',
        imageUrl: '',
        activated: true,
        langKey: 'fr',
        createdBy: 'system',
        createdDate: null,
        lastModifiedBy: 'system',
        lastModifiedDate: null,
        authorities: ['ROLE_USER']
    }
];

app.get('/api/tickets/mine', (req: Request, res: Response) => {
    res.json(tickets.filter(t => (t.assignedToId = 4)));
});

app.get('/api/tickets', (req: Request, res: Response) => {
    res.header(
        'link',
        'link: </api/tickets?eagerload=false&page=0&size=20>; rel="last",</api/tickets?eagerload=false&page=0&size=20>; rel="first"'
    );
    res.header('X-Total-Count', tickets.length.toString());
    res.json(tickets);
});

app.get('/api/tickets/:id', (req: Request, res: Response) => {
    res.json(tickets.find(t => t.id === req.params.id));
});

app.get('/api/projects', (req: Request, res: Response) => {
    res.json(projects);
});

app.get('/api/projects/:id', (req: Request, res: Response) => {
    res.json(projects.find(p => p.id === req.params.id));
});

app.get('/api/labels', (req: Request, res: Response) => {
    res.json(labels);
});

app.get('/api/labels/:id', (req: Request, res: Response) => {
    res.json(labels.find(l => l.id === req.params.id));
});

app.get('/api/account', (req: Request, res: Response) => {
    const token = jwt.verify(req.header('Authorization').substr('Bearer '.length), 'secret');
    res.json(users.find(u => u.login === token.sub));
});

app.post('/api/authenticate', (req: Request, res: Response) => {
    const user = users.find(u => u.login === req.body.username);

    const token = jwt.sign(
        {
            sub: user.login,
            auth: user.authorities,
            exp: Math.floor(Date.now() / 1000) + 60 * 60
        },
        'secret'
    );

    res.header('authorization', 'Bearer ' + token);

    res.json({
        id_token: token
    });
});

app.listen(port, function() {
    console.log('Example app listening on port ' + port + '!');
});
