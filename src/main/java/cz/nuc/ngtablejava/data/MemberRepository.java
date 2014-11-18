/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cz.nuc.ngtablejava.data;

import cz.nuc.ngtablejava.model.Member;
import cz.nuc.ngtablejava.rest.FilterParam;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.xml.ws.Holder;
import java.util.List;

@ApplicationScoped
public class MemberRepository {

    @Inject
    private EntityManager em;

    public List<Member> filterItems(FilterParam param, Holder<Long> totalCountHolder) {
        MemberFilterer filter = new MemberFilterer(em, param);
        totalCountHolder.value = filter.count();
        return filter.list();

    }
}
