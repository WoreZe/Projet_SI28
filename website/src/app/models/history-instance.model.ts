export class HistoryInstanceModel {
  id: number;
  history: History;
  currentStep: HistoryStep;
  state: string;
  joinCode: string;
  pathsInfos: any;
  users: any;
  gameOwner?: ReducedUser;
  createdAt: number;

  constructor(data: any) {
    this.id = data.id;
    this.history = new History(data.history);
    this.currentStep = new HistoryStep(data.currentStep);
    this.state = data.state;
    this.joinCode = data.joinCode;
    this.pathsInfos = data.pathsInfos;
    this.users = Object.values(data.users).map((user: any) => new HistoryUser(user));
    this.gameOwner = data.gameOwner ? new ReducedUser(data.gameOwner) : undefined;
    this.createdAt = data.createdAt;
  }

  isFull() {
    return this.history.maxPlayers === this.users.length;
  }
}

export class HistoryUser {
  avatar: string;
  currentResponse: any;
  uniqueId: string;
  username: string;
  hasResponseValue: boolean = false;
  hasResponse: boolean = false;

  constructor(data: any) {
    this.avatar = data.avatar;
    this.currentResponse = data.currentResponse;
    this.uniqueId = data.uniqueId;
    this.username = data.username;
    this.hasResponseValue = data.hasResponseValue;
    this.hasResponse = data.hasResponse;
  }
}

export class History {
  id: number;
  entrypoint: HistoryStep;
  creator: ReducedUser;
  title: string;
  description: string;
  imageUrl: string;
  visibility: string;
  maxPlayers: number;
  minPlayers: number;

  constructor(props: any) {
    this.id = props.id;
    this.entrypoint = props.entrypoint;
    this.creator = new ReducedUser(props.creator);
    this.title = props.title;
    this.description = props.description;
    this.imageUrl = props.imageUrl;
    this.visibility = props.visibility;
    this.maxPlayers = props.maxPlayers;
    this.minPlayers = props.minPlayers;
  }
}

//"timeLimit": 0,
// "id": 1,
// "title": "Recalled to Life",
// "type": "SCRIPTED_CHOICE",
// "slug": "dolore_cumque",
// "updatedAt": 1717915611647

export class HistoryStep {
  id: number;
  title: string;
  type: string;
  slug: string;
  updatedAt: number;
  timeLimit?: number;

  constructor(data: any) {
    this.id = data.id;
    this.title = data.title;
    this.type = data.type;
    this.slug = data.slug;
    this.updatedAt = data.updatedAt;
    this.timeLimit = data.timeLimit;
  }

  isTimeUnlimited() {
    return this.timeLimit === 0 || this.timeLimit === undefined;
  }
}

export class HistoryStepFull extends HistoryStep {
  scriptController: string;
  page: {
    rendererType: "FREEMARKER";
    content: string;
  };
  isEntryPoint: boolean = false;

  constructor(data: any) {
    super(data);
    this.scriptController = data.scriptController;
    this.page = data.page;
  }
}

export class ReducedUser {
  username: string;
  avatar: string;

  constructor(data: any) {
    this.username = data.username;
    this.avatar = data.avatar;
  }
}
