import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';

import {Observable} from 'rxjs';

import {Subject} from '../../models/subject.model';
import {environment} from '../../../environments/environment';
import {Student} from '../../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class StudentService {

  constructor(private http: HttpClient) {
  }

  getTimetable(username: string): Observable<Subject[]> {
    return this.http.get<Subject[]>(environment.apiUrl + 'students/' + username + '/subjects');
  }

  manageStudentSubject(student: Student): Observable<Student> {
    return this.http.patch<Student>(environment.apiUrl + 'students/subjects', student);
  }
}
