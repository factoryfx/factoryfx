export class StoredDataMetadata {
    creationTime?: Date;
    id?: string;
    user?: string;
    comment?: string;

    mapFromJson(json: any): StoredDataMetadata{
        this.creationTime=json.creationTime;
        this.id=json.id;
        this.user=json.user;
        this.comment=json.comment;
        return this;
    }
}