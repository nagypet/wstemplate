/*
 * Copyright header
 * The ultimate spring based webservice template project.
 * Author Peter Nagy <nagy.peter.home@gmail.com>
 */

export class CertInfo
{
  issuerCN: string;
  subjectCN: string;
  validFrom: Date;
  validTo: Date;
  valid: boolean;
}


export class KeystoreEntry
{
  constructor(
    public alias: string,
    public password: string,
    public inUse: boolean,
    public type: string,
    public valid: boolean,
    public chain: CertInfo[])
  {}

  getTypeAbbr(): string
  {
    switch (this.type)
    {
      case "PRIVATE_KEY_ENTRY":
        return "PK";

      default:
        return "";
    }
  }
}


export class CertificateFile
{
  constructor(
    public content: string,
    public password: string
  ) {}
}
