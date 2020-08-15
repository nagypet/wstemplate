/*
 * Copyright header
 * The ultimate spring based webservice template project.
 * Author Peter Nagy <nagy.peter.home@gmail.com>
 */

import {Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges} from '@angular/core';
import {AdminService} from '../../admin.service';
import {CertInfo, KeystoreEntry} from '../../../modell/keystore';
import {GlobalService} from '../../global.service';
import {NgbActiveModal, NgbModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'ngbd-modal-content',
  template: `
    <div class="modal-header">
      <h4 class="modal-title">Removing certificate</h4>
    </div>
    <div class="modal-body">
      <p>The certificate <b>{{name}}</b> will be removed!</p>
    </div>
    <div class="modal-footer">
      <button type="button" class="btn btn-light" (click)="activeModal.close('OK')">OK</button>
      <button type="button" class="btn btn-light" (click)="activeModal.close('Cancel')">Cancel</button>
    </div>
  `
})
export class NgbdModalContent
{
  @Input() name;

  constructor(
    public activeModal: NgbActiveModal
  ) {}
}


@Component({
  selector: 'app-keystore',
  templateUrl: './keystore.component.html',
  styleUrls: ['./keystore.component.css']
})
export class KeystoreComponent implements OnInit, OnChanges
{
  @Input('Keystore') keystore: Array<KeystoreEntry>;
  @Input('DeleteAllowed') deleteAllowed: boolean = true;
  @Output('Selected') certSelected = new EventEmitter<KeystoreEntry>();
  @Output('OnDelete') onDeleteEmitter = new EventEmitter<string>();

  selected: KeystoreEntry;

  constructor(
    public adminService: AdminService,
    public globalService: GlobalService,
    private modalService: NgbModal
  ) { }

  ngOnInit()
  {
  }

  onSelectEntry(entry: KeystoreEntry)
  {
    this.selected = entry;
    this.certSelected.emit(entry);
  }

  isEntrySelected(entry: KeystoreEntry): boolean
  {
    if (this.selected == null)
    {
      return false;
    }
    return (entry.alias === this.selected.alias);
  }

  onSelectCert(entry: CertInfo)
  {
  }

  isCertSelected(entry: CertInfo): boolean
  {
    return false;
  }

  ngOnChanges(changes: SimpleChanges): void
  {
    if (this.keystore.length === 0)
    {
      this.selected = null;
    }
  }
  
  getClass(entry: KeystoreEntry)
  {
    let classes = 'list-group-item list-group-item-action d-flex justify-content-between';

    if (entry.inUse === true)
    {
      classes += ' list-group-item-primary';
    }
    else if (entry.valid !== true)
    {
      classes += ' list-group-item-warning';
    }
    else 
    {
      classes += ' list-group-item-light';
    }

    return classes;
  }

  onDelete(entry: KeystoreEntry)
  {
    //const modalRef = this.modalService.open(content, { centered: true });
    const modalRef = this.modalService.open(NgbdModalContent, { centered: true });
    modalRef.componentInstance.name = entry.alias;
    modalRef.result.then((data) =>
    {
      if (data === 'OK')
      {
        this.onDeleteEmitter.emit(entry.alias);
      }
    }).catch(() => console.log('Cancelled'));
  }

}
