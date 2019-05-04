/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {Component, OnInit} from '@angular/core';
import {NgbActiveModal} from "@ng-bootstrap/ng-bootstrap";
import * as Bowser from "bowser";

@Component({
  selector: 'app-resident-key-requirement-dialog',
  templateUrl: './resident-key-requirement-dialog.component.html',
  styleUrls: ['./resident-key-requirement-dialog.component.css']
})
export class ResidentKeyRequirementDialogComponent implements OnInit {

  private bowser = Bowser.getParser(window.navigator.userAgent);

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit() {
  }

  isChromeForWindows(): boolean{
    return this.bowser.satisfies({windows: { chrome: '>0' } })
  }

  isChromeForMac(): boolean{
    return this.bowser.satisfies({macos: { chrome: '>0' } })
  }

  isChromeForAndroid(): boolean{
    return this.bowser.satisfies({android: { chrome: '>0' } })
  }

  isChromeForIOS(): boolean{
    return this.bowser.satisfies({ios: { chrome: '>0' } })
  }

  isFirefoxForWindows(): boolean{
    return this.bowser.satisfies({windows: { firefox: '>0' } })
  }

  isFirefoxForMac(): boolean{
    return this.bowser.satisfies({macos: { firefox: '>0' } })
  }

  isFirefoxForAndroid(): boolean{
    return this.bowser.satisfies({android: { firefox: '>0' } })
  }

  isFirefoxForIOS(): boolean{
    return this.bowser.satisfies({ios: { firefox: '>0' } })
  }

  isSafariForMac(): boolean{
    return this.bowser.satisfies({macos: { safari: '>0' } })
  }

  isSafariForIOS(): boolean{
    return this.bowser.satisfies({ios: { safari: '>0' } })
  }

}
